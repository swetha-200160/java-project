package com.can.buyerApp.service.impl;

import com.can.buyerApp.entity.ContextEntity;
import com.can.buyerApp.entity.InsuranceCategoryEntity;
import com.can.buyerApp.entity.Progress;
import com.can.buyerApp.entity.UserInfo;
import com.can.buyerApp.repository.*;
import com.can.buyerApp.request.MotorSearchRequest;
import com.can.buyerApp.service.MotorSearchService;
import com.can.buyerApp.utils.DateTimeUtils;
import com.can.buyerApp.webclient.OndcWebClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.can.buyerApp.constants.PreConstants.*;

@Slf4j
@Service
public class MotorSearchServiceImpl implements MotorSearchService {

    private final OndcWebClient ondcWebClient;
    private final PaymentTagRepo paymentTagRepo;
    private final UserInfoRepository userDetailsRepository;
    private final ProgressRepository progressRepository;
    private final InsuranceCategoryRepository categoryRepository;
    private final ContextRepository contextRepository;

    public MotorSearchServiceImpl(OndcWebClient ondcWebClient,
                                  PaymentTagRepo paymentTagRepo,
                                  UserInfoRepository userDetailsRepository,
                                  ProgressRepository progressRepository, InsuranceCategoryRepository categoryRepository, ContextRepository contextRepository) {
        this.ondcWebClient = ondcWebClient;
        this.paymentTagRepo = paymentTagRepo;
        this.userDetailsRepository = userDetailsRepository;
        this.progressRepository = progressRepository;
        this.categoryRepository = categoryRepository;
        this.contextRepository = contextRepository;
    }

    @Value("${header.subscriberId}")
    private String subscriberId;

    @Value("${api.bap.url}")
    private String apiBapUrl;

    @Override
    public ResponseEntity<?> sendMotorSearchRequest(String domain, String type, Long userId, String agentId) {
        try {
            MotorSearchRequest searchRequest = createMotorSearchRequest(domain, type, userId, agentId);
            log.info("Formed First Motor Search Request: {}", searchRequest);
            ResponseEntity<?> response = ondcWebClient.sendFirstMotorSearchRequest(searchRequest);
            return ResponseEntity.ok(response.getBody());

        } catch (Exception e) {
            log.error("Error in processing first motor search request: ", e);
            throw new RuntimeException("Error in processing first motor search request", e);
        }
    }


    @Override
    public MotorSearchRequest createMotorSearchRequest(String domain, String type, Long userId, String agentId) {
        MotorSearchRequest requestDTO = new MotorSearchRequest();
        String messageId = UUID.randomUUID().toString();
        String transactionId = UUID.randomUUID().toString();

        Optional<UserInfo> data = userDetailsRepository.findById(userId);
        if (!data.isPresent()) {
            throw new RuntimeException("User not found with id: " + userId);
        }
        UserInfo userDetails = data.get();

        // Save progress
        Progress progress = new Progress();
        progress.setUserId(userDetails.getId());
        progress.setTransactionId(transactionId);
        progress.setCreatedAt(LocalDateTime.now());
        progress.setUpdatedAt(LocalDateTime.now());
        progressRepository.save(progress);

        // Context setup with motor-specific configurations
        MotorSearchRequest.Context context = new MotorSearchRequest.Context();
        context.setAction(SEARCH);
        context.setBap_id(subscriberId);
        context.setBap_uri(apiBapUrl);
        context.setDomain(domain);
        context.setMessage_id(messageId);
        context.setTimestamp(DateTimeUtils.getCurrentFormattedTimestamp());
        context.setTransaction_id(transactionId);
        context.setTtl(MOTOR_TTL);  // Changed: Use P24H instead of PT15S
        context.setVersion(MOTOR_VERSION);  // Changed: Use 2.0.1 instead of 2.0.0

        MotorSearchRequest.Context.Location location = new MotorSearchRequest.Context.Location();
        MotorSearchRequest.Context.Location.Country country = new MotorSearchRequest.Context.Location.Country();
        country.setCode(COUNTRY_CODE);
        MotorSearchRequest.Context.Location.City city = new MotorSearchRequest.Context.Location.City();
        city.setCode(CITY_CODE);

        location.setCountry(country);
        location.setCity(city);
        context.setLocation(location);
        requestDTO.setContext(context);

        // Message setup
        MotorSearchRequest.Message message = new MotorSearchRequest.Message();
        MotorSearchRequest.Message.Intent intent = new MotorSearchRequest.Message.Intent();

        // Intent - Category
        MotorSearchRequest.Message.Intent.Category category = new MotorSearchRequest.Message.Intent.Category();
        MotorSearchRequest.Message.Intent.Category.Descriptor categoryDescriptor =
                new MotorSearchRequest.Message.Intent.Category.Descriptor();
        categoryDescriptor.setCode(type);
        category.setDescriptor(categoryDescriptor);
        intent.setCategory(category);

        // Intent - Fulfillment with Agent (NEW: Added agent support)
        if (agentId != null && !agentId.isEmpty()) {
            MotorSearchRequest.Message.Intent.Fulfillment fulfillment =
                    new MotorSearchRequest.Message.Intent.Fulfillment();
            MotorSearchRequest.Message.Intent.Fulfillment.Agent agent =
                    new MotorSearchRequest.Message.Intent.Fulfillment.Agent();
            MotorSearchRequest.Message.Intent.Fulfillment.Agent.Person person =
                    new MotorSearchRequest.Message.Intent.Fulfillment.Agent.Person();
            person.setId(agentId);
            agent.setPerson(person);
            fulfillment.setAgent(agent);
            intent.setFulfillment(fulfillment);
        }

        // Intent - Payment
        MotorSearchRequest.Message.Intent.Payment payment = new MotorSearchRequest.Message.Intent.Payment();
        payment.setCollected_by(BPP);

        List<MotorSearchRequest.Message.Intent.Payment.Tag> tags = paymentTagRepo.findAllWithDetails().stream()
                .map(dbTag -> {
                    MotorSearchRequest.Message.Intent.Payment.Tag tag = new MotorSearchRequest.Message.Intent.Payment.Tag();
                    MotorSearchRequest.Message.Intent.Payment.Tag.Descriptor tagDescriptor =
                            new MotorSearchRequest.Message.Intent.Payment.Tag.Descriptor();
                    tagDescriptor.setCode(dbTag.getDescriptorType());
                    tag.setDescriptor(tagDescriptor);
                    tag.setDisplay(dbTag.isRequired());

                    List<MotorSearchRequest.Message.Intent.Payment.Tag.TagListItem> tagListItems =
                            dbTag.getDetails().stream()
                                    .filter(dbDetail -> !Arrays.asList(SETTLEMENT_TYPE, SETTLEMENT_AMOUNT)
                                            .contains(dbDetail.getDescriptorType()))
                                    .map(dbDetail -> {
                                        MotorSearchRequest.Message.Intent.Payment.Tag.TagListItem listItem =
                                                new MotorSearchRequest.Message.Intent.Payment.Tag.TagListItem();
                                        MotorSearchRequest.Message.Intent.Payment.Tag.Descriptor listItemDescriptor =
                                                new MotorSearchRequest.Message.Intent.Payment.Tag.Descriptor();
                                        listItemDescriptor.setCode(dbDetail.getDescriptorType());
                                        listItem.setDescriptor(listItemDescriptor);
                                        listItem.setValue(dbDetail.getValue());
                                        return listItem;
                                    })
                                    .collect(Collectors.toList());

                    if (!tagListItems.isEmpty()) {
                        tag.setList(tagListItems);
                        return tag;
                    }
                    return null;
                })
                .filter(tag -> tag != null)
                .collect(Collectors.toList());

        payment.setTags(tags);
        intent.setPayment(payment);

        message.setIntent(intent);
        requestDTO.setMessage(message);

        return requestDTO;
    }

    @Override
    public ResponseEntity<?> sendSecondMotorSearchRequest(String domain, String type,
                                                          String transactionId, String messageId,
                                                          String submissionId, String providerId,
                                                          String formStatus, String formId, String categoryId) {
        try {
            MotorSearchRequest searchRequest = createSecondMotorSearchRequest(
                    domain, type, transactionId, messageId, submissionId,
                    providerId, formStatus, formId,categoryId);

            log.info("Formed Second Motor Search Request: {}", searchRequest);
            ResponseEntity<?> response = ondcWebClient.sendSecondMotorSearch(searchRequest);
            return ResponseEntity.ok(response.getBody());

//            //For Development Testing puprose
//            ObjectMapper objectMapper = new ObjectMapper();
//            objectMapper.registerModule(new JavaTimeModule());
//            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//
//            String jsonRequest = objectMapper
//                    .writerWithDefaultPrettyPrinter()
//                    .writeValueAsString(searchRequest);
//
//            log.info("Formed First Motor Search Request JSON:\n{}", jsonRequest);
//
//            return ResponseEntity.ok(jsonRequest);

        } catch (Exception e) {
            log.error("Error in processing second motor search request", e);
            throw new RuntimeException("Error in processing second motor search request", e);
        }
    }



    @Override
    public MotorSearchRequest createSecondMotorSearchRequest(String domain, String type,
                                                             String transactionId, String messageId,
                                                             String submissionId, String providerId,
                                                             String formStatus, String formId, String categoryId) {
        MotorSearchRequest searchRequest = new MotorSearchRequest();

        // Fetch category details first to validate formId
        List<InsuranceCategoryEntity> categoryEntityList = categoryRepository
                .findByTransactionIdAndProviderIdAndMessageIdAndCategoryId(
                        transactionId, providerId, messageId, categoryId);

        if (categoryEntityList.isEmpty()) {
            throw new RuntimeException("No categories found for transaction: " + transactionId +
                    ", categoryId: " + categoryId);
        }

        // Get the category entity and validate formId
        InsuranceCategoryEntity categoryEntity = categoryEntityList.get(0);
        String actualFormId = categoryEntity.getForm_id();

        if (actualFormId == null) {
            throw new RuntimeException("Form ID not found in category for transaction: " + transactionId);
        }

        // Optional: Validate if the passed formId matches the one in database
        if (formId != null && !formId.equals(actualFormId)) {
            log.warn("Passed formId {} doesn't match database formId {}. Using database formId.",
                    formId, actualFormId);
        }

        // Use the formId from database (insurance_category table)
        String validatedFormId = actualFormId;

        // Fetch context details WITHOUT using formId in query
        ContextEntity contextDetails = contextRepository
                .findByTransactionAndProviderIdAndMessageId(transactionId, providerId, messageId);

        if (contextDetails == null) {
            throw new RuntimeException("Context not found for transaction: " + transactionId +
                    ", provider: " + providerId);
        }

        // Mark context as selected
        contextDetails.setSelected(true);
        contextRepository.save(contextDetails);

        // Generate new message ID for second search
        String newMessageId = UUID.randomUUID().toString();

        // Build Context
        MotorSearchRequest.Context context = new MotorSearchRequest.Context();
        context.setAction(SEARCH);
        context.setBap_id(contextDetails.getBap_id());
        context.setBap_uri(apiBapUrl);
        context.setBpp_id(contextDetails.getBpp_id());
        context.setBpp_uri(contextDetails.getBpp_uri());
        context.setDomain(domain);
        context.setMessage_id(newMessageId);
        context.setTimestamp(DateTimeUtils.getCurrentFormattedTimestamp());
        context.setTransaction_id(transactionId);
        context.setTtl(MOTOR_TTL);  // P24H for motor insurance
        context.setVersion(MOTOR_VERSION);  // 2.0.1 for motor insurance

        // Set Location
        MotorSearchRequest.Context.Location location = new MotorSearchRequest.Context.Location();
        MotorSearchRequest.Context.Location.Country country = new MotorSearchRequest.Context.Location.Country();
        country.setCode(contextDetails.getLocation_country_code());
        location.setCountry(country);

        MotorSearchRequest.Context.Location.City city = new MotorSearchRequest.Context.Location.City();
        city.setCode(CITY_CODE);
        location.setCity(city);

        context.setLocation(location);

        // Build Message
        MotorSearchRequest.Message message = new MotorSearchRequest.Message();
        MotorSearchRequest.Message.Intent intent = new MotorSearchRequest.Message.Intent();

        // Set Category
        MotorSearchRequest.Message.Intent.Category category = new MotorSearchRequest.Message.Intent.Category();
        MotorSearchRequest.Message.Intent.Category.Descriptor descriptor =
                new MotorSearchRequest.Message.Intent.Category.Descriptor();
        descriptor.setCode(type);
        category.setDescriptor(descriptor);
        intent.setCategory(category);

        // Set Provider with form response
        MotorSearchRequest.Message.Intent.Provider provider = new MotorSearchRequest.Message.Intent.Provider();
        provider.setId(providerId);

        // Create Items list
        List<MotorSearchRequest.Message.Intent.Provider.Item> items = new ArrayList<>();

        // Use the category entity we already fetched
        MotorSearchRequest.Message.Intent.Provider.Item item =
                new MotorSearchRequest.Message.Intent.Provider.Item();
        item.setId(categoryEntity.getItemId());

        // Set XInput with form response
        MotorSearchRequest.Message.Intent.Provider.Item.XInput xinput =
                new MotorSearchRequest.Message.Intent.Provider.Item.XInput();

        MotorSearchRequest.Message.Intent.Provider.Item.XInput.Form form =
                new MotorSearchRequest.Message.Intent.Provider.Item.XInput.Form();
        form.setId(validatedFormId);  // Using validated formId from insurance_category table
        xinput.setForm(form);

        MotorSearchRequest.Message.Intent.Provider.Item.XInput.FormResponse formResponse =
                new MotorSearchRequest.Message.Intent.Provider.Item.XInput.FormResponse();
        formResponse.setStatus(formStatus);
        formResponse.setSubmission_id(submissionId);
        xinput.setForm_response(formResponse);

        item.setXinput(xinput);
        items.add(item);

        provider.setItems(items);
        intent.setProvider(provider);

        // Set Payment with tags
        MotorSearchRequest.Message.Intent.Payment payment = new MotorSearchRequest.Message.Intent.Payment();
        payment.setCollected_by(BPP);

        List<MotorSearchRequest.Message.Intent.Payment.Tag> tags = paymentTagRepo.findAllWithDetails().stream()
                .map(dbTag -> {
                    MotorSearchRequest.Message.Intent.Payment.Tag tag =
                            new MotorSearchRequest.Message.Intent.Payment.Tag();
                    MotorSearchRequest.Message.Intent.Payment.Tag.Descriptor tagDescriptor =
                            new MotorSearchRequest.Message.Intent.Payment.Tag.Descriptor();
                    tagDescriptor.setCode(dbTag.getDescriptorType());
                    tag.setDescriptor(tagDescriptor);
                    tag.setDisplay(dbTag.isRequired());

                    List<MotorSearchRequest.Message.Intent.Payment.Tag.TagListItem> tagListItems =
                            dbTag.getDetails().stream()
                                    .filter(dbDetail -> !Arrays.asList(SETTLEMENT_TYPE, SETTLEMENT_AMOUNT)
                                            .contains(dbDetail.getDescriptorType()))
                                    .map(dbDetail -> {
                                        MotorSearchRequest.Message.Intent.Payment.Tag.TagListItem listItem =
                                                new MotorSearchRequest.Message.Intent.Payment.Tag.TagListItem();
                                        MotorSearchRequest.Message.Intent.Payment.Tag.Descriptor listItemDescriptor =
                                                new MotorSearchRequest.Message.Intent.Payment.Tag.Descriptor();
                                        listItemDescriptor.setCode(dbDetail.getDescriptorType());
                                        listItem.setDescriptor(listItemDescriptor);
                                        listItem.setValue(dbDetail.getValue());
                                        return listItem;
                                    })
                                    .collect(Collectors.toList());

                    if (!tagListItems.isEmpty()) {
                        tag.setList(tagListItems);
                        return tag;
                    }
                    return null;
                })
                .filter(tag -> tag != null)
                .collect(Collectors.toList());

        payment.setTags(tags);
        intent.setPayment(payment);

        message.setIntent(intent);

        searchRequest.setContext(context);
        searchRequest.setMessage(message);

        return searchRequest;
    }
//    @Override
//    public MotorSearchRequest createSecondMotorSearchRequest(String domain, String type,
//                                                             String transactionId, String messageId,
//                                                             String submissionId, String providerId,
//                                                             String formStatus, String formId, String categoryId) {
//        MotorSearchRequest searchRequest = new MotorSearchRequest();
//
//        // Fetch context details from database
//        ContextEntity contextDetails = contextRepository
//                .findByTransactionAndProviderIdAndMessageIdAndFormId(transactionId, providerId, messageId, formId);
//
//        if (contextDetails == null) {
//            throw new RuntimeException("Context not found for transaction: " + transactionId +
//                    ", provider: " + providerId + ", formId: " + formId);
//        }
//
//        // Fetch category details
//        List<InsuranceCategoryEntity> categoryEntityList = categoryRepository
//                .findByTransactionIdAndProviderIdAndMessageIdAndCategoryId(transactionId, providerId, messageId,categoryId);
//
//        if (categoryEntityList.isEmpty()) {
//            throw new RuntimeException("No categories found for transaction: " + transactionId);
//        }
//
//        // Mark context as selected
//        contextDetails.setSelected(true);
//        contextRepository.save(contextDetails);
//
//        // Generate new message ID for second search
//        String newMessageId = UUID.randomUUID().toString();
//
//        // Build Context
//        MotorSearchRequest.Context context = new MotorSearchRequest.Context();
//        context.setAction(SEARCH);
//        context.setBap_id(contextDetails.getBap_id());
//        context.setBap_uri(apiBapUrl);
//        context.setBpp_id(contextDetails.getBpp_id());
//        context.setBpp_uri(contextDetails.getBpp_uri());
//        context.setDomain(domain);
//        context.setMessage_id(newMessageId);
//        context.setTimestamp(DateTimeUtils.getCurrentFormattedTimestamp());
//        context.setTransaction_id(transactionId);
//        context.setTtl(MOTOR_TTL);  // P24H for motor insurance
//        context.setVersion(MOTOR_VERSION);  // 2.0.1 for motor insurance
//
//        // Set Location
//        MotorSearchRequest.Context.Location location = new MotorSearchRequest.Context.Location();
//        MotorSearchRequest.Context.Location.Country country = new MotorSearchRequest.Context.Location.Country();
//        country.setCode(contextDetails.getLocation_country_code());
//        location.setCountry(country);
//
//        MotorSearchRequest.Context.Location.City city = new MotorSearchRequest.Context.Location.City();
//        city.setCode(CITY_CODE);
//        location.setCity(city);
//
//        context.setLocation(location);
//
//        // Build Message
//        MotorSearchRequest.Message message = new MotorSearchRequest.Message();
//        MotorSearchRequest.Message.Intent intent = new MotorSearchRequest.Message.Intent();
//
//        // Set Category
//        MotorSearchRequest.Message.Intent.Category category = new MotorSearchRequest.Message.Intent.Category();
//        MotorSearchRequest.Message.Intent.Category.Descriptor descriptor =
//                new MotorSearchRequest.Message.Intent.Category.Descriptor();
//        descriptor.setCode(type);
//        category.setDescriptor(descriptor);
//        intent.setCategory(category);
//
//        // Set Provider with form response
//        MotorSearchRequest.Message.Intent.Provider provider = new MotorSearchRequest.Message.Intent.Provider();
//        provider.setId(providerId);
//
//        // Create Items list
//        List<MotorSearchRequest.Message.Intent.Provider.Item> items = new ArrayList<>();
//
//        // Find the item that matches the formId
//        InsuranceCategoryEntity matchingCategory = categoryEntityList.stream()
//                .filter(cat -> formId.equals(cat.getForm_id()))
//                .findFirst()
//                .orElse(categoryEntityList.get(0));  // Fallback to first if not found
//
//        // Create Item with form response
//        MotorSearchRequest.Message.Intent.Provider.Item item =
//                new MotorSearchRequest.Message.Intent.Provider.Item();
//        item.setId(matchingCategory.getItemId());
//
//        // Set XInput with form response
//        MotorSearchRequest.Message.Intent.Provider.Item.XInput xinput =
//                new MotorSearchRequest.Message.Intent.Provider.Item.XInput();
//
//        MotorSearchRequest.Message.Intent.Provider.Item.XInput.Form form =
//                new MotorSearchRequest.Message.Intent.Provider.Item.XInput.Form();
//        form.setId(formId);
//        xinput.setForm(form);
//
//        MotorSearchRequest.Message.Intent.Provider.Item.XInput.FormResponse formResponse =
//                new MotorSearchRequest.Message.Intent.Provider.Item.XInput.FormResponse();
//        formResponse.setStatus(formStatus);
//        formResponse.setSubmission_id(submissionId);
//        xinput.setForm_response(formResponse);
//
//        item.setXinput(xinput);
//        items.add(item);
//
//        provider.setItems(items);
//        intent.setProvider(provider);
//
//        // Set Payment with tags
//        MotorSearchRequest.Message.Intent.Payment payment = new MotorSearchRequest.Message.Intent.Payment();
//        payment.setCollected_by(BPP);
//
//        List<MotorSearchRequest.Message.Intent.Payment.Tag> tags = paymentTagRepo.findAllWithDetails().stream()
//                .map(dbTag -> {
//                    MotorSearchRequest.Message.Intent.Payment.Tag tag =
//                            new MotorSearchRequest.Message.Intent.Payment.Tag();
//                    MotorSearchRequest.Message.Intent.Payment.Tag.Descriptor tagDescriptor =
//                            new MotorSearchRequest.Message.Intent.Payment.Tag.Descriptor();
//                    tagDescriptor.setCode(dbTag.getDescriptorType());
//                    tag.setDescriptor(tagDescriptor);
//                    tag.setDisplay(dbTag.isRequired());
//
//                    List<MotorSearchRequest.Message.Intent.Payment.Tag.TagListItem> tagListItems =
//                            dbTag.getDetails().stream()
//                                    .filter(dbDetail -> !Arrays.asList(SETTLEMENT_TYPE, SETTLEMENT_AMOUNT)
//                                            .contains(dbDetail.getDescriptorType()))
//                                    .map(dbDetail -> {
//                                        MotorSearchRequest.Message.Intent.Payment.Tag.TagListItem listItem =
//                                                new MotorSearchRequest.Message.Intent.Payment.Tag.TagListItem();
//                                        MotorSearchRequest.Message.Intent.Payment.Tag.Descriptor listItemDescriptor =
//                                                new MotorSearchRequest.Message.Intent.Payment.Tag.Descriptor();
//                                        listItemDescriptor.setCode(dbDetail.getDescriptorType());
//                                        listItem.setDescriptor(listItemDescriptor);
//                                        listItem.setValue(dbDetail.getValue());
//                                        return listItem;
//                                    })
//                                    .collect(Collectors.toList());
//
//                    if (!tagListItems.isEmpty()) {
//                        tag.setList(tagListItems);
//                        return tag;
//                    }
//                    return null;
//                })
//                .filter(tag -> tag != null)
//                .collect(Collectors.toList());
//
//        payment.setTags(tags);
//        intent.setPayment(payment);
//
//        message.setIntent(intent);
//
//        searchRequest.setContext(context);
//        searchRequest.setMessage(message);
//
//        return searchRequest;
//    }
}