package com.can.buyerApp.service.impl;

import com.can.buyerApp.entity.ContextEntity;
import com.can.buyerApp.entity.GeneralInformation;
import com.can.buyerApp.entity.SearchCatalog;
import com.can.buyerApp.repository.ContextRepository;
import com.can.buyerApp.repository.GeneralInformationRepo;
import com.can.buyerApp.repository.SearchCatalogRepository;
import com.can.buyerApp.request.MotorSelectRequest;
import com.can.buyerApp.service.MotorSelectService;
import com.can.buyerApp.utils.DateTimeUtils;
import com.can.buyerApp.webclient.OndcWebClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.can.buyerApp.constants.PreConstants.CITY_CODE;
import static com.can.buyerApp.constants.PreConstants.SELECT;

@Slf4j
@Service
public class MotorSelectServiceImpl implements MotorSelectService {

    private final OndcWebClient ondcWebClient;
    private final ContextRepository contextRepository;
    private final SearchCatalogRepository motorSearchCatalogRepository;
    private final GeneralInformationRepo generalInformationRepo;

    @Value("${api.bap.url}")
    private String apiBapUrl;

    public MotorSelectServiceImpl(OndcWebClient ondcWebClient,
                                  ContextRepository contextRepository,
                                  SearchCatalogRepository motorSearchCatalogRepository,
                                  GeneralInformationRepo generalInformationRepo) {
        this.ondcWebClient = ondcWebClient;
        this.contextRepository = contextRepository;
        this.motorSearchCatalogRepository = motorSearchCatalogRepository;
        this.generalInformationRepo = generalInformationRepo;
    }

    @Override
    public ResponseEntity<?> sendSelectRequest(String domain, String transactionId, List<String> addons,
                                               String itemId, String formStatus,
                                               String formId, String submissionId) {
        try {
            MotorSelectRequest selectRequest = createSelectRequest(domain, transactionId, addons,
                    itemId, formStatus, formId, submissionId);  // PASS formId and submissionId

            // Uncomment for production
             ResponseEntity<?> response = ondcWebClient.sendMotorSelect(selectRequest);
             return ResponseEntity.ok(response.getBody());

//            // For Development Testing purpose
//            ObjectMapper objectMapper = new ObjectMapper();
//            objectMapper.registerModule(new JavaTimeModule());
//            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//
//            String jsonRequest = objectMapper
//                    .writerWithDefaultPrettyPrinter()
//                    .writeValueAsString(selectRequest);
//
//            log.info("Formed Motor Select Request JSON:\n{}", jsonRequest);
//
//            return ResponseEntity.ok(jsonRequest);

        } catch (Exception e) {
            log.error("Error in processing Motor Insurance Select request", e);
            throw new RuntimeException("Error in processing Motor Insurance Select request", e);
        }
    }

    @Override
    public MotorSelectRequest createSelectRequest(String domain, String transactionId, List<String> addons,
                                                  String itemId, String formStatus,
                                                  String formId, String submissionId) {


        List<SearchCatalog> catalogData = motorSearchCatalogRepository
                .findByTransactionId(transactionId);


        if (catalogData == null || catalogData.isEmpty()) {
            throw new RuntimeException("No catalog data found for transaction ID: " + transactionId);
        }

        SearchCatalog catalog = catalogData.get(0);
        ContextEntity contextDetails = contextRepository.findByTransactionAndIsSelected(transactionId);
        MotorSelectRequest request = new MotorSelectRequest();

        String messageId = UUID.randomUUID().toString();

        // Setting context
        MotorSelectRequest.Context context = new MotorSelectRequest.Context();
        context.setAction(SELECT);
        context.setBap_id(contextDetails.getBap_id());
        context.setBap_uri(apiBapUrl);
        context.setBpp_id(contextDetails.getBpp_id());
        context.setBpp_uri(contextDetails.getBpp_uri());
        context.setDomain(domain);
        context.setMessage_id(messageId);
        context.setTimestamp(DateTimeUtils.getCurrentFormattedTimestamp());
        context.setTransaction_id(transactionId);
        context.setTtl(contextDetails.getTtl());
        context.setVersion(contextDetails.getVersion());

        // Setting location
        MotorSelectRequest.Context.Location location = new MotorSelectRequest.Context.Location();
        MotorSelectRequest.Context.Location.Country country = new MotorSelectRequest.Context.Location.Country();
        country.setCode(contextDetails.getLocation_country_code());
        location.setCountry(country);
        MotorSelectRequest.Context.Location.City city = new MotorSelectRequest.Context.Location.City();
        city.setCode(CITY_CODE);
        location.setCity(city);
        context.setLocation(location);
        request.setContext(context);

        // Setting message
        MotorSelectRequest.Message message = new MotorSelectRequest.Message();
        MotorSelectRequest.Message.Order order = new MotorSelectRequest.Message.Order();

        // Setting items
        List<MotorSelectRequest.Message.Order.Item> items = new ArrayList<>();
        MotorSelectRequest.Message.Order.Item item = new MotorSelectRequest.Message.Order.Item();
        item.setId(itemId);
        item.setParent_item_id(catalog.getParentItemId());

        // Fetch IDV_VALUE from GeneralInformation
        String idvValue = fetchIdvValue(catalog.getId());

        // Setting tags for GENERAL_INFO (IDV_SELECTED) - ONLY FOR FIRST SELECT
        if (StringUtils.isBlank(formId)) {  // First select - no form yet
            List<MotorSelectRequest.Message.Order.Item.Tag> tags = new ArrayList<>();
            MotorSelectRequest.Message.Order.Item.Tag generalInfoTag = new MotorSelectRequest.Message.Order.Item.Tag();

            MotorSelectRequest.Message.Order.Item.Tag.Descriptor generalInfoDescriptor =
                    new MotorSelectRequest.Message.Order.Item.Tag.Descriptor();
            generalInfoDescriptor.setCode("GENERAL_INFO");
            generalInfoDescriptor.setName("General Information");
            generalInfoTag.setDescriptor(generalInfoDescriptor);

            List<MotorSelectRequest.Message.Order.Item.Tag.TagValue> generalInfoValues = new ArrayList<>();
            MotorSelectRequest.Message.Order.Item.Tag.TagValue idvTagValue =
                    new MotorSelectRequest.Message.Order.Item.Tag.TagValue();

            MotorSelectRequest.Message.Order.Item.Tag.TagValue.Descriptor idvDescriptor =
                    new MotorSelectRequest.Message.Order.Item.Tag.TagValue.Descriptor();
            idvDescriptor.setCode("IDV_SELECTED");
            idvTagValue.setDescriptor(idvDescriptor);
            idvTagValue.setValue(idvValue);
            generalInfoValues.add(idvTagValue);

            generalInfoTag.setList(generalInfoValues);
            tags.add(generalInfoTag);
            item.setTags(tags);
        }

        // ADD XINPUT WITH FORM AND SUBMISSION ID (for 2nd and 3rd select)
        if (StringUtils.isNotBlank(formId) && StringUtils.isNotBlank(submissionId)) {
            MotorSelectRequest.Message.Order.Item.XInput xinput =
                    new MotorSelectRequest.Message.Order.Item.XInput();

            MotorSelectRequest.Message.Order.Item.XInput.Form form =
                    new MotorSelectRequest.Message.Order.Item.XInput.Form();
            form.setId(formId);
            xinput.setForm(form);

            MotorSelectRequest.Message.Order.Item.XInput.FormResponse formResponse =
                    new MotorSelectRequest.Message.Order.Item.XInput.FormResponse();
            formResponse.setStatus(formStatus);
            formResponse.setSubmission_id(submissionId);
            xinput.setForm_response(formResponse);

            item.setXinput(xinput);
        }

        // Setting add-ons
        if (addons == null) {
            addons = new ArrayList<>();
        }

        List<MotorSelectRequest.Message.Order.Item.AddOn> addOns = new ArrayList<>();
        for (String addonId : addons) {
            MotorSelectRequest.Message.Order.Item.AddOn addOn =
                    new MotorSelectRequest.Message.Order.Item.AddOn();
            addOn.setId(addonId);

            MotorSelectRequest.Message.Order.Item.AddOn.Quantity quantity =
                    new MotorSelectRequest.Message.Order.Item.AddOn.Quantity();
            MotorSelectRequest.Message.Order.Item.AddOn.Quantity.Selected selected =
                    new MotorSelectRequest.Message.Order.Item.AddOn.Quantity.Selected();
            selected.setCount(1);
            quantity.setSelected(selected);

            addOn.setQuantity(quantity);
            addOns.add(addOn);
        }
        item.setAdd_ons(addOns);
        items.add(item);
        order.setItems(items);

        // Setting provider
        MotorSelectRequest.Message.Order.Provider provider = new MotorSelectRequest.Message.Order.Provider();
        provider.setId(contextDetails.getProviderId());
        order.setProvider(provider);

        message.setOrder(order);
        request.setMessage(message);

        return request;
    }

    /**
     * Fetch IDV_VALUE from GeneralInformation based on SearchCatalog ID
     */
    private String fetchIdvValue(Long searchCatalogId) {
        try {
            List<GeneralInformation> generalInfoList = generalInformationRepo.findBySearchCatalogId(searchCatalogId);

            if (generalInfoList != null && !generalInfoList.isEmpty()) {
                return generalInfoList.stream()
                        .filter(info -> "IDV_VALUE".equalsIgnoreCase(info.getCode()))
                        .findFirst()
                        .map(GeneralInformation::getValue)
                        .orElse(null);
            }

            log.warn("No IDV_VALUE found for searchCatalogId: {}", searchCatalogId);
            return null;

        } catch (Exception e) {
            log.error("Error fetching IDV_VALUE for searchCatalogId: {}", searchCatalogId, e);
            return null;
        }
    }
}