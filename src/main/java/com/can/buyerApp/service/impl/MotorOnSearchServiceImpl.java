package com.can.buyerApp.service.impl;

import com.can.buyerApp.constants.MotorInsuranceConstants;
import com.can.buyerApp.constants.PreConstants;
import com.can.buyerApp.dto.*;
import com.can.buyerApp.entity.*;
import com.can.buyerApp.exception.TransactionIdNotFoundException;
import com.can.buyerApp.mapper.RequestMapper;
import com.can.buyerApp.repository.*;
import com.can.buyerApp.request.MotorOnSearchRequest;
import com.can.buyerApp.service.MotorOnSearchService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.can.buyerApp.constants.MotorInsuranceConstants.FOUR_WHEELER_INSURANCE;
import static com.can.buyerApp.constants.MotorInsuranceConstants.TWO_WHEELER_INSURANCE;

@Slf4j
@Service
public class MotorOnSearchServiceImpl implements MotorOnSearchService {

    @Value("${api.bap.url}")
    private String bapUrl;

    private final InsuranceCategoryRepository insuranceCategoryRepository;
    private final SearchCatalogRepository searchCatalogRepository;
    private final ContextRepository contextRepository;
    private final ObjectMapper objectMapper;
    private final RequestMapper requestMapper;
    private final GeneralInformationRepo generalInformationRepo;
    private final AddOnsRepository addOnsRepository;
    private final RestTemplate restTemplate;
    private final ProgressRepository progressRepository;


    public MotorOnSearchServiceImpl(InsuranceCategoryRepository insuranceCategoryRepository,
                                    SearchCatalogRepository searchCatalogRepository,
                                    ContextRepository contextRepository,
                                    ObjectMapper objectMapper,
                                    RequestMapper requestMapper,
                                    GeneralInformationRepo generalInformationRepo,
                                    AddOnsRepository addOnsRepository,
                                    RestTemplate restTemplate,
                                    ProgressRepository progressRepository) {
        this.insuranceCategoryRepository = insuranceCategoryRepository;
        this.searchCatalogRepository = searchCatalogRepository;
        this.contextRepository = contextRepository;
        this.objectMapper = objectMapper;
        this.requestMapper = requestMapper;
        this.generalInformationRepo = generalInformationRepo;
        this.addOnsRepository = addOnsRepository;
        this.restTemplate = restTemplate;
        this.progressRepository = progressRepository;
    }

    @Override
    @Transactional
    public ResponseEntity<?> saveMotorOnSearchRequest(MotorOnSearchRequest request) {
        try {

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(request);

            log.info("Incoming Motor on_search request:\n{}", json);

//            String response = objectMapper.writeValueAsString(request);
//            log.info("MOTOR_ON_SEARCH_RESPONSE: {}", response);

            List<MotorOnSearchRequest.Message.Catalog.Provider.Item.XInput> forms =
                    request.getMessage().getCatalog().getProviders().stream()
                            .flatMap(provider -> provider.getItems().stream())
                            .map(MotorOnSearchRequest.Message.Catalog.Provider.Item::getXinput)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());

            if (!forms.isEmpty()) {
                log.info("Processing Motor On Search 1 (with forms)");
                handleMotorOnSearch1(request, forms);
                Acknowledgement acknowledgement = requestMapper.searchAckResponse(request);
                return ResponseEntity.ok(acknowledgement);
            } else {
                log.info("Processing Motor On Search 2 (without forms - quotes)");
                handleMotorOnSearch2(request);
                Acknowledgement acknowledgement = requestMapper.searchAckResponse(request);
                return ResponseEntity.ok(acknowledgement);
            }
        } catch (Exception e) {
            log.error("Error saving motor on search response: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error saving motor on search response.");
        }
    }

    @Async
    private void handleMotorOnSearch1(MotorOnSearchRequest request,
                                      List<MotorOnSearchRequest.Message.Catalog.Provider.Item.XInput> forms) {
        try {
            MotorOnSearchRequest.Message.Catalog.Provider provider =
                    request.getMessage().getCatalog().getProviders().get(0);

            String providerId = provider.getId();
            String providerName = provider.getDescriptor().getName();
            String providerUrl = provider.getDescriptor().getImages().get(0).getUrl();


            // Save context once per provider (not per item)
            ContextEntity context = mapToContextEntity(
                    request.getContext(), providerId, providerName, providerUrl);
            contextRepository.save(context);
            log.info("Saved motor context details");

            // Get categories map for easy lookup
            Map<String, MotorOnSearchRequest.Message.Catalog.Provider.Category> categoryMap =
                    provider.getCategories().stream()
                            .collect(Collectors.toMap(
                                    MotorOnSearchRequest.Message.Catalog.Provider.Category::getId,
                                    cat -> cat
                            ));


            // Process each item
            for (MotorOnSearchRequest.Message.Catalog.Provider.Item item : provider.getItems()) {
                String itemId = item.getId();

                // Find the parent category (TWO_WHEELER_INSURANCE or FOUR_WHEELER_INSURANCE)
                String parentCategoryId = null;

                for (String categoryId : item.getCategory_ids()) {
                    MotorOnSearchRequest.Message.Catalog.Provider.Category category = categoryMap.get(categoryId);
                    if (category != null) {
                        String categoryCode = category.getDescriptor().getCode();

                        if (TWO_WHEELER_INSURANCE.equals(categoryCode) || FOUR_WHEELER_INSURANCE.equals(categoryCode)) {
                            parentCategoryId = categoryId;
                            log.info("Found specific parent category: {} - {} for item: {}",
                                    categoryId, categoryCode, itemId);
                            break;
                        }

                        if ("MOTOR_INSURANCE".equals(categoryCode) && parentCategoryId == null) {
                            parentCategoryId = categoryId;
                            log.info("Found generic parent category: {} - {} for item: {}",
                                    categoryId, categoryCode, itemId);
                        }
                    }
                }

                // Process each category_id associated with this item
                for (String categoryId : item.getCategory_ids()) {
                    MotorOnSearchRequest.Message.Catalog.Provider.Category category =
                            categoryMap.get(categoryId);

                    if (category == null) {
                        log.warn("Category not found for ID: {}", categoryId);
                        continue;
                    }

                    String categoryCode = category.getDescriptor().getCode();

                    // Skip parent categories and MOTOR_INSURANCE - we only store the specific insurance types
                    if (MotorInsuranceConstants.isParentCategory(categoryCode)) {
                        continue;
                    }

                    // Determine vehicle type and coverage type
                    String vehicleType = MotorInsuranceConstants.getVehicleType(categoryCode);
                    String coverageType = MotorInsuranceConstants.getCoverageType(categoryCode);

                    if (vehicleType == null) {
                        log.warn("Could not determine vehicle type for category: {}", categoryCode);
                        continue;
                    }

                    log.info("Processing category: {} - Vehicle: {}, Coverage: {}",
                            categoryCode, vehicleType, coverageType);

                    // Create insurance category entity with parent category info
                    InsuranceCategoryEntity insuranceCategory =
                            mapToMotorInsuranceCategoryEntity(
                                    category, item, provider, request.getContext(),
                                    vehicleType, coverageType, parentCategoryId, forms);

                    insuranceCategoryRepository.save(insuranceCategory);
                    log.info("Saved motor insurance category: {} for vehicle type: {} with parent: {}",
                            categoryCode, vehicleType, parentCategoryId);
                }
            }

            // Update progress
            Optional<Progress> progressOpt = progressRepository
                    .findByTransactionId(request.getContext().getTransaction_id());
            if (progressOpt.isPresent()) {
                Progress progress = progressOpt.get();
                progress.setStatus(PreConstants.ON_SEARCH_1);
                progress.setUpdatedAt(LocalDateTime.now());
                progressRepository.save(progress);
            }
        } catch (Exception e) {
            log.error("Error saving MotorOnSearch1 request: ", e);
            throw new RuntimeException("Error saving MotorOnSearch1 request", e);
        }
    }

    private ContextEntity mapToContextEntity(MotorOnSearchRequest.Context context,
                                             String providerId, String providerName,
                                             String providerUrl) {
        try {
            ContextEntity entity = new ContextEntity();
            entity.setBap_id(context.getBap_id());
            entity.setBap_uri(context.getBap_uri());
            entity.setBpp_id(context.getBpp_id());
            entity.setBpp_uri(context.getBpp_uri());
            entity.setDomain(context.getDomain());
            entity.setLocation_country_code(context.getLocation().getCountry().getCode());
            entity.setMessage_id(context.getMessage_id());
            entity.setTimestamp(context.getTimestamp());
            entity.setTransaction_id(context.getTransaction_id());
            entity.setTtl(context.getTtl());
            entity.setVersion(context.getVersion());
            entity.setProviderId(providerId);
            entity.setProviderName(providerName);
            entity.setProviderUrl(providerUrl);
            entity.setCreatedAt(LocalDateTime.now());
            entity.setUpdatedAt(LocalDateTime.now());
            return entity;
        } catch (Exception e) {
            log.error("Error mapping context entity: ", e);
            throw new RuntimeException("Error mapping context entity", e);
        }
    }

    private InsuranceCategoryEntity mapToMotorInsuranceCategoryEntity(
            MotorOnSearchRequest.Message.Catalog.Provider.Category category,
            MotorOnSearchRequest.Message.Catalog.Provider.Item item,
            MotorOnSearchRequest.Message.Catalog.Provider provider,
            MotorOnSearchRequest.Context context,
            String vehicleType,
            String coverageType,
            String parentCategoryId,
            List<MotorOnSearchRequest.Message.Catalog.Provider.Item.XInput> forms) {

        try {
            InsuranceCategoryEntity entity = new InsuranceCategoryEntity();

            // Category details
            entity.setCategory_id(category.getId());
            entity.setCategory_name(category.getDescriptor().getName());
            entity.setVehicleType(vehicleType);
            entity.setCoverageType(coverageType);

            // Set parent category ID (C11 for two-wheeler, C10 for four-wheeler)
            entity.setParentCategoryId(parentCategoryId);

            // Provider details
            entity.setProviderId(provider.getId());
            entity.setProviderName(provider.getDescriptor().getName());

            // Item details
            entity.setItemId(item.getId());
            entity.setItemName(item.getDescriptor().getName());
            entity.setItemShortDesc(item.getDescriptor().getShort_desc());

            // Duration
            if (item.getTime() != null) {
                entity.setDuration(item.getTime().getDuration());
                entity.setDurationLabel(item.getTime().getLabel());
            }

            // Form details
            if (item.getXinput() != null && item.getXinput().getForm() != null) {
                MotorOnSearchRequest.Message.Catalog.Provider.Item.XInput form = item.getXinput();
                forms.stream().forEach(formdata -> {
                    entity.setForm_id(form.getForm().getId());
                    entity.setMime_type(form.getForm().getMime_type());
                    entity.setForm_url(form.getForm().getUrl());
                    entity.setResubmit(String.valueOf(form.getForm().isResubmit()));
                    entity.setMultiple_submissions(String.valueOf(form.getForm().isMultiple_submissions()));
                });
            }

            // General information from tags
            Map<String, String> generalInfo = processMotorGeneralInformation(item.getTags());
            entity.setGeneralInformation(objectMapper.writeValueAsString(generalInfo));

            // Transaction details
            entity.setMessage_id(context.getMessage_id());
            entity.setTransactionId(context.getTransaction_id());
            entity.setCreatedAt(LocalDateTime.now());
            entity.setUpdatedAt(LocalDateTime.now());

            return entity;
        } catch (Exception e) {
            log.error("Error mapping motor insurance category", e);
            throw new RuntimeException("Error mapping motor insurance category", e);
        }
    }

    private Map<String, String> processMotorGeneralInformation(
            List<MotorOnSearchRequest.Message.Catalog.Provider.Item.Tag> tags) {
        Map<String, String> generalInfoMap = new LinkedHashMap<>();
        if (tags != null) {
            for (MotorOnSearchRequest.Message.Catalog.Provider.Item.Tag tag : tags) {
                if (tag.getList() != null) {
                    for (MotorOnSearchRequest.Message.Catalog.Provider.Item.Tag.Detail detail : tag.getList()) {
                        String key = detail.getDescriptor().getCode();
                        String val = detail.getValue();
                        if (key != null) {
                            generalInfoMap.put(key, val);
                        }
                    }
                }
            }
        }
        return generalInfoMap;
    }

    @Override
    public ResponseEntity<?> getMotorInsuranceCategoryByTransactionId(String transactionId,
                                                                      String providerId,
                                                                      String messageId) {
        List<InsuranceCategoryEntity> data = insuranceCategoryRepository
                .findByTransactionIdAndProviderIdAndMessageId(transactionId, providerId, messageId);

        if (data.isEmpty()) {
            throw new TransactionIdNotFoundException("Transaction ID not found");
        }

        String transaction = data.get(0).getTransactionId();
        String provider = data.get(0).getProviderId();
        String formId = data.get(0).getForm_id();

        for (InsuranceCategoryEntity category : data) {
            String formUrl = category.getForm_url();
            String url = bapUrl + "form-url?transactionId=" +
                    transaction + "&providerId=" + provider + "&formUrl=" + formUrl +
                    "&messageId=" + messageId + "&formId=" + formId;
            category.setForm_url(url);
        }
        return ResponseEntity.ok().body(data);
    }

    @Override
    public ResponseEntity<?> getMotorInsuranceCategoriesByVehicleType(
            String transactionId, String messageId, String vehicleType) {

        List<InsuranceCategoryEntity> data = insuranceCategoryRepository
                .findByTransactionIdAndMessageIdAndVehicleType(transactionId, messageId, vehicleType);

        if (data.isEmpty()) {
            throw new TransactionIdNotFoundException(
                    "No " + vehicleType + " insurance categories found for transaction ID: " + transactionId);
        }

        // Update form URLs
        for (InsuranceCategoryEntity category : data) {
            String formUrl = category.getForm_url();
            String url = bapUrl + "form-url?transactionId=" +
                    transactionId + "&providerId=" + category.getProviderId() +
                    "&formUrl=" + formUrl + "&messageId=" + messageId +
                    "&formId=" + category.getForm_id() + "&vehicleType=" + vehicleType;
            category.setForm_url(url);
        }

        return ResponseEntity.ok().body(data);
    }

    @Async
    private void handleMotorOnSearch2(MotorOnSearchRequest request) {
        try {
            String transactionId = request.getContext().getTransaction_id();
            String messageId = request.getContext().getMessage_id();

            MotorOnSearchRequest.Message.Catalog.Provider provider =
                    request.getMessage().getCatalog().getProviders().get(0);

            for (MotorOnSearchRequest.Message.Catalog.Provider.Item item : provider.getItems()) {

                // VERY IMPORTANT: skip parent catalog items
                if (item.getParent_item_id() == null) {
                    continue;
                }

                // AVE ONLY QUOTES
                SearchCatalog searchCatalog = new SearchCatalog();
                searchCatalog.setTransactionId(transactionId);
                searchCatalog.setMessageId(messageId);

                // CHILD item id
                searchCatalog.setItemId(item.getId());

                // REAL parent item id
                searchCatalog.setParentItemId(item.getParent_item_id());

                searchCatalog.setItemName(item.getDescriptor().getName());

                if (item.getPrice() != null) {
                    searchCatalog.setPrice(item.getPrice().getValue());
                }

                searchCatalog.setCreatedAt(LocalDateTime.now());
                searchCatalog.setUpdatedAt(LocalDateTime.now());

                searchCatalog = searchCatalogRepository.save(searchCatalog);

                /* -------- TAGS -------- */
                if (item.getTags() != null) {
                    for (MotorOnSearchRequest.Message.Catalog.Provider.Item.Tag tag : item.getTags()) {
                        if (tag.getList() == null) continue;

                        for (MotorOnSearchRequest.Message.Catalog.Provider.Item.Tag.Detail detail : tag.getList()) {
                            GeneralInformation gi = new GeneralInformation();
                            gi.setCode(detail.getDescriptor().getCode());
                            gi.setValue(detail.getValue());
                            gi.setSearchCatalog(searchCatalog);
                            generalInformationRepo.save(gi);
                        }
                    }
                }

                /* -------- ADDONS -------- */
                if (item.getAdd_ons() != null) {
                    for (MotorOnSearchRequest.Message.Catalog.Provider.Item.AddOn addOn : item.getAdd_ons()) {
                        AddonDetail ad = new AddonDetail();
                        ad.setAddonId(addOn.getId());
                        ad.setAddonName(addOn.getDescriptor().getName());

                        if (addOn.getPrice() != null) {
                            ad.setAddonPrice(addOn.getPrice().getValue());
                        }

                        ad.setSearchCatalog(searchCatalog);
                        addOnsRepository.save(ad);
                    }
                }
            }

            log.info("Saved motor on search 2 response");

            // Update progress
            progressRepository.findByTransactionId(transactionId)
                    .ifPresent(progress -> {
                        progress.setStatus(PreConstants.ON_SEARCH_2);
                        progress.setUpdatedAt(LocalDateTime.now());
                        progressRepository.save(progress);
                    });

        } catch (Exception e) {
            log.error("Error processing MotorOnSearch2 request: ", e);
            throw new RuntimeException("Error processing MotorOnSearch2 request", e);
        }
    }


    @Override
    public List<SearchCatalogDTO> getMotorCatalogs(String transactionId, String messageId) {
        List<SearchCatalog> catalogItems = searchCatalogRepository
                .findTop2ByTransactionIdOrderByCreatedAtDesc(transactionId);

        if (catalogItems.isEmpty()) {
            throw new TransactionIdNotFoundException("Transaction ID not found");
        }

        return catalogItems.stream()
                .filter(catalog -> catalog.getPrice() != null)
                .map(catalog -> {
                    Map<String, String> generalInfoMap = catalog.getGeneralInformationList().stream()
                            .collect(Collectors.toMap(
                                    GeneralInformation::getCode,
                                    GeneralInformation::getValue
                            ));

                    List<AddonDetailDTO> addonDetailsDTO = catalog.getAddonDetails().stream()
                            .map(addon -> new AddonDetailDTO(
                                    addon.getAddonId(),
                                    addon.getAddonName(),
                                    addon.getAddonPrice()
                            ))
                            .collect(Collectors.toList());

                    return new SearchCatalogDTO(
                            catalog.getId(),
                            catalog.getTransactionId(),
                            catalog.getMessageId(),
                            catalog.getItemId(),
                            catalog.getItemName(),
                            catalog.getPrice(),
                            generalInfoMap,
                            addonDetailsDTO
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ProviderDTO> getMotorProviderList(String transactionId, String messageId) {
        List<ContextEntity> providers = contextRepository
                .findByTransactionIdAndMessageId(transactionId, messageId);

        if (providers.isEmpty()) {
            throw new TransactionIdNotFoundException("Transaction ID not found");
        }

        // Get unique providers (avoid duplicates)
        Map<String, ContextEntity> uniqueProviders = new LinkedHashMap<>();
        for (ContextEntity provider : providers) {
            uniqueProviders.putIfAbsent(provider.getProviderId(), provider);
        }

        List<ProviderDTO> providerDTOList = new ArrayList<>();
        for (ContextEntity details : uniqueProviders.values()) {
            ProviderDTO providerDTO = new ProviderDTO();
            providerDTO.setId(details.getId());
            providerDTO.setProviderId(details.getProviderId());
            providerDTO.setProviderName(details.getProviderName());
            providerDTO.setTransactionId(details.getTransaction_id());
            providerDTO.setProviderUrl(details.getProviderUrl());
            providerDTOList.add(providerDTO);
        }
        return providerDTOList;
    }


    @Override
    public Map<String, String> getMotorForm(
            String transactionId,
            String providerId,
            String formUrl,
            String messageId,
            String formId,
            String categoryId) {

        Map<String, String> data = new LinkedHashMap<>();

        // STEP 1: Extract seller form URL if wrapped
        String sellerFormUrl = extractSellerFormUrl(formUrl);

        log.info("Final Seller Form URL: {}", sellerFormUrl);

        // STEP 2: Resolve relative URL using BPP URI
//        ContextEntity context = contextRepository
//                .findByTransactionAndProviderIdAndMessageId(
//                        transactionId, providerId, messageId);

        ContextEntity context = contextRepository
                .findByTransactionAndProviderId(transactionId, providerId);

        if (context == null) {
            throw new IllegalStateException("Context not found");
        }

        String bppUri = context.getBpp_uri();

        String resolvedFormUrl =
                sellerFormUrl.startsWith("http")
                        ? sellerFormUrl
                        : bppUri + (sellerFormUrl.startsWith("/") ? "" : "/") + sellerFormUrl;

        // STEP 3: Call seller form
        ResponseEntity<String> response =
                restTemplate.getForEntity(resolvedFormUrl, String.class);

        data.put("HTML", response.getBody());

        // STEP 4: Extract submit URL
        Document doc = Jsoup.parse(response.getBody());
        Element form = doc.selectFirst("form");

        if (form != null) {
            String action = form.attr("action");
            data.put("Submit-Url",
                    action.startsWith("http")
                            ? action
                            : bppUri + (action.startsWith("/") ? "" : "/") + action);
        } else {
            data.put("Submit-Url", resolvedFormUrl);
        }

        return data;
    }

    private String extractSellerFormUrl(String formUrl) {

        try {
            URI uri = new URI(formUrl);
            String query = uri.getQuery();

            if (query == null) {
                return formUrl;
            }

            for (String param : query.split("&")) {
                String[] kv = param.split("=", 2);
                if (kv.length == 2 && kv[0].equals("formUrl")) {
                    return URLDecoder.decode(kv[1], StandardCharsets.UTF_8);
                }
            }

        } catch (Exception e) {
            log.error("Failed to extract seller form URL", e);
        }

        return formUrl;
    }


    @Override
    public List<String> getAvailableVehicleTypes(String transactionId) {
        List<InsuranceCategoryEntity> categories =
                insuranceCategoryRepository.findByTransactionId(transactionId);

        if (categories.isEmpty()) {
            throw new TransactionIdNotFoundException("Transaction ID not found");
        }

        return categories.stream()
                .map(InsuranceCategoryEntity::getVehicleType)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }


    public List<SearchQuoteResponse> getQuotesByTransactionId(String transactionId) {

        List<SearchCatalog> catalogs =
                searchCatalogRepository.findByTransactionId(transactionId);

        List<SearchQuoteResponse> responseList = new ArrayList<>();

        for (SearchCatalog catalog : catalogs) {

            SearchQuoteResponse response = new SearchQuoteResponse();
            response.setTransactionId(catalog.getTransactionId());
            response.setMessageId(catalog.getMessageId());
            response.setItemId(catalog.getItemId());
            response.setParentItemId(catalog.getParentItemId());
            response.setItemName(catalog.getItemName());
            response.setPrice(catalog.getPrice());

            /* -------- GENERAL INFO -------- */
            List<GeneralInfoDTO> giList =
                    generalInformationRepo
                            .findBySearchCatalogId(catalog.getId())
                            .stream()
                            .map(gi -> {
                                GeneralInfoDTO dto = new GeneralInfoDTO();
                                dto.setCode(gi.getCode());
                                dto.setValue(gi.getValue());
                                return dto;
                            })
                            .toList();

            response.setGeneralInfo(giList);

            //-------- ADDONS -------- */
            List<AddonDTO> addonList =
                    addOnsRepository
                            .findBySearchCatalogId(catalog.getId())
                            .stream()
                            .map(ad -> {
                                AddonDTO dto = new AddonDTO();
                                dto.setAddonId(ad.getAddonId());
                                dto.setAddonName(ad.getAddonName());
                                dto.setAddonPrice(ad.getAddonPrice());
                                return dto;
                            })
                            .toList();

            response.setAddons(addonList);

            responseList.add(response);
        }

        return responseList;
    }
}