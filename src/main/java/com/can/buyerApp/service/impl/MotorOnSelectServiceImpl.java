package com.can.buyerApp.service.impl;

import com.can.buyerApp.constants.PreConstants;
import com.can.buyerApp.dto.Acknowledgement;
import com.can.buyerApp.entity.MotorOnSelectEntity;
import com.can.buyerApp.entity.Progress;
import com.can.buyerApp.exception.TransactionIdNotFoundException;
import com.can.buyerApp.mapper.RequestMapper;
import com.can.buyerApp.repository.MotorOnSelectRepository;
import com.can.buyerApp.repository.ProgressRepository;
import com.can.buyerApp.request.MotorOnselectRequest;
import com.can.buyerApp.service.MotorOnSelectService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MotorOnSelectServiceImpl implements MotorOnSelectService {

    @Value("${api.bap.url}")
    private String bapUrl;


    private final MotorOnSelectRepository motorOnSelectRepository;
    private final ObjectMapper objectMapper;
    private final RequestMapper requestMapper;
    private final ProgressRepository progressRepository;

    public MotorOnSelectServiceImpl(MotorOnSelectRepository motorOnSelectRepository,
                                    ObjectMapper objectMapper,
                                    RequestMapper requestMapper,
                                    ProgressRepository progressRepository) {
        this.motorOnSelectRepository = motorOnSelectRepository;
        this.objectMapper = objectMapper;
        this.requestMapper = requestMapper;
        this.progressRepository = progressRepository;
    }

    @Override
    @Transactional
    public ResponseEntity<?> saveSelectRequest(MotorOnselectRequest motorOnselectRequest) {

        if (Objects.isNull(motorOnselectRequest) || Objects.isNull(motorOnselectRequest.getMessage())) {
            log.info("MotorOnselectRequest or Message cannot be null");
            throw new RuntimeException("Invalid request: MotorOnselectRequest or Message cannot be null.");
        }

        MotorOnselectRequest.Message.Order order = motorOnselectRequest.getMessage().getOrder();
        if (Objects.isNull(order)) {
            throw new RuntimeException("Invalid request: Order cannot be null.");
        }

        MotorOnselectRequest.Message.Order.Quote quote = order.getQuote();
        if (Objects.isNull(quote)) {
            throw new RuntimeException("Invalid request: Quote cannot be null.");
        }

        try {
            String response = objectMapper.writeValueAsString(motorOnselectRequest);
            log.info("MOTOR_ON_SELECT_1_RESPONSE: " + response);

            List<MotorOnSelectEntity> entitiesToSave = new ArrayList<>();
            List<MotorOnselectRequest.Message.Order.Item> items = order.getItems();

            if (items != null && !items.isEmpty()) {
                for (MotorOnselectRequest.Message.Order.Item item : items) {
                    MotorOnSelectEntity entity = new MotorOnSelectEntity();

                    // Basic item details
                    entity.setItemId(item.getId());
                    entity.setTransaction_id(motorOnselectRequest.getContext().getTransaction_id());
                    entity.setMessageId(motorOnselectRequest.getContext().getMessage_id());
                    entity.setParentItemId(item.getParent_item_id());

                    // Item descriptor
                    if (item.getDescriptor() != null) {
                        entity.setItemName(item.getDescriptor().getName());
                        entity.setItemShortDesc(item.getDescriptor().getShort_desc());

                        // Item images
                        if (item.getDescriptor().getImages() != null && !item.getDescriptor().getImages().isEmpty()) {
                            List<Map<String, String>> imagesList = new ArrayList<>();
                            for (MotorOnselectRequest.Message.Order.Item.Descriptor.Image image :
                                    item.getDescriptor().getImages()) {
                                Map<String, String> imageMap = new HashMap<>();
                                imageMap.put("url", image.getUrl());
                                imageMap.put("size_type", image.getSize_type());
                                imagesList.add(imageMap);
                            }
                            entity.setItemImages(objectMapper.writeValueAsString(imagesList));
                        }
                    }

                    // Item price
                    if (item.getPrice() != null) {
                        entity.setItemPrice(item.getPrice().getValue());
                        entity.setItemCurrency(item.getPrice().getCurrency());
                    }

                    // Time duration
                    if (item.getTime() != null) {
                        entity.setTimeDuration(item.getTime().getDuration());
                        entity.setTimeLabel(item.getTime().getLabel());
                    }

                    // XInput form details
                    if (item.getXinput() != null) {
                        entity.setXinputRequired(item.getXinput().isRequired());

                        if (item.getXinput().getHead() != null) {
                            if (item.getXinput().getHead().getDescriptor() != null) {
                                entity.setXinputHeadName(item.getXinput().getHead().getDescriptor().getName());
                            }
                            if (item.getXinput().getHead().getHeadings() != null) {
                                entity.setXinputHeadings(objectMapper.writeValueAsString(
                                        item.getXinput().getHead().getHeadings()));
                            }
                        }

                        if (item.getXinput().getForm() != null) {
                            entity.setFormId(item.getXinput().getForm().getId());
                            entity.setFormUrl(item.getXinput().getForm().getUrl());
                            entity.setFormMimeType(item.getXinput().getForm().getMime_type());
                            entity.setFormSubmissionId(item.getXinput().getForm().getSubmission_id());
                            entity.setFormResubmit(item.getXinput().getForm().isResubmit());
                            entity.setFormMultipleSubmissions(item.getXinput().getForm().isMultiple_sumbissions());
                        }
                    }

                    entity.setCreatedAt(LocalDateTime.now());
                    entity.setUpdatedAt(LocalDateTime.now());

                    // Process vehicle and policy details from tags
                    Map<String, Object> vehicleDetailsMap = processVehicleDetails(item.getTags());
                    entity.setVehicleDetails(objectMapper.writeValueAsString(vehicleDetailsMap));

                    // Process add-ons
                    List<MotorOnselectRequest.Message.Order.Item.AddOn> addOns = item.getAdd_ons();
                    if (addOns != null && !addOns.isEmpty()) {
                        List<Map<String, Object>> addonDetails = new ArrayList<>();
                        for (MotorOnselectRequest.Message.Order.Item.AddOn addOn : addOns) {
                            Map<String, Object> addonMap = new HashMap<>();
                            addonMap.put("addonId", addOn.getId());

                            if (addOn.getDescriptor() != null) {
                                addonMap.put("addonName", addOn.getDescriptor().getName());
                                addonMap.put("addonCode", addOn.getDescriptor().getCode());
                            }

                            if (addOn.getPrice() != null) {
                                addonMap.put("priceValue", addOn.getPrice().getValue());
                                addonMap.put("currency", addOn.getPrice().getCurrency());
                            }

                            if (addOn.getQuantity() != null && addOn.getQuantity().getSelected() != null) {
                                addonMap.put("selectedCount", addOn.getQuantity().getSelected().getCount());
                            }

                            addonDetails.add(addonMap);
                        }
                        entity.setAddOns(objectMapper.writeValueAsString(addonDetails));
                    }

                    // Quote details
                    entity.setQuoteId(quote.getId());
                    entity.setTotalPrice(quote.getPrice().getValue());
                    entity.setCurrency(quote.getPrice().getCurrency());
                    entity.setTtl(quote.getTtl());

                    // Breakup details (store all breakup items)
                    Map<String, Object> breakupDetailsMap = new LinkedHashMap<>();
                    if (quote.getBreakup() != null) {
                        for (MotorOnselectRequest.Message.Order.Quote.Breakup breakup : quote.getBreakup()) {
                            Map<String, Object> breakupItem = new LinkedHashMap<>();

                            if (breakup.getPrice() != null) {
                                breakupItem.put("value", breakup.getPrice().getValue());
                                breakupItem.put("currency", breakup.getPrice().getCurrency());
                            }

                            // If this breakup is for ADD_ONS, include the item details
                            if ("ADD_ONS".equalsIgnoreCase(breakup.getTitle()) && breakup.getItem() != null) {
                                Map<String, Object> itemDetails = new HashMap<>();
                                itemDetails.put("id", breakup.getItem().getId());
                                if (breakup.getItem().getParent_item_id() != null) {
                                    itemDetails.put("parent_item_id", breakup.getItem().getParent_item_id());
                                }
                                if (breakup.getItem().getAdd_ons() != null) {
                                    List<String> addonIds = breakup.getItem().getAdd_ons().stream()
                                            .map(MotorOnselectRequest.Message.Order.Quote.Breakup.BreakupItem.AddOn::getId)
                                            .collect(Collectors.toList());
                                    itemDetails.put("add_on_ids", addonIds);
                                }
                                breakupItem.put("item", itemDetails);
                            }

                            breakupDetailsMap.put(breakup.getTitle(), breakupItem);
                        }
                    }
                    entity.setBreakupDetails(objectMapper.writeValueAsString(breakupDetailsMap));

                    // Provider details
                    if (order.getProvider() != null) {
                        entity.setProviderId(order.getProvider().getId());

                        if (order.getProvider().getDescriptor() != null) {
                            entity.setProviderName(order.getProvider().getDescriptor().getName());
                            entity.setProviderShortDesc(order.getProvider().getDescriptor().getShort_desc());
                            entity.setProviderLongDesc(order.getProvider().getDescriptor().getLong_desc());

                            if (order.getProvider().getDescriptor().getImages() != null &&
                                    !order.getProvider().getDescriptor().getImages().isEmpty()) {
                                List<Map<String, String>> providerImages = new ArrayList<>();
                                for (MotorOnselectRequest.Message.Order.Provider.Descriptor.Image image :
                                        order.getProvider().getDescriptor().getImages()) {
                                    Map<String, String> imageMap = new HashMap<>();
                                    imageMap.put("url", image.getUrl());
                                    if (image.getSize_type() != null) {
                                        imageMap.put("size_type", image.getSize_type());
                                    }
                                    providerImages.add(imageMap);
                                }
                                entity.setProviderImages(objectMapper.writeValueAsString(providerImages));
                            }
                        }
                    }

                    // Fulfillment/Customer details
                    if (order.getFulfillments() != null && !order.getFulfillments().isEmpty()) {
                        MotorOnselectRequest.Message.Order.Fulfillment fulfillment = order.getFulfillments().get(0);

                        entity.setFulfillmentId(fulfillment.getId());
                        entity.setFulfillmentType(fulfillment.getType());

                        if (fulfillment.getState() != null && fulfillment.getState().getDescriptor() != null) {
                            entity.setFulfillmentState(fulfillment.getState().getDescriptor().getCode());
                        }

                        if (fulfillment.getCustomer() != null) {
                            Map<String, String> customerDetails = new HashMap<>();

                            if (fulfillment.getCustomer().getPerson() != null) {
                                customerDetails.put("name", fulfillment.getCustomer().getPerson().getName());
                                customerDetails.put("gender", fulfillment.getCustomer().getPerson().getGender());
                                customerDetails.put("dob", fulfillment.getCustomer().getPerson().getDob());
                            }

                            if (fulfillment.getCustomer().getContact() != null) {
                                customerDetails.put("phone", fulfillment.getCustomer().getContact().getPhone());
                                customerDetails.put("email", fulfillment.getCustomer().getContact().getEmail());
                            }

                            entity.setCustomerDetails(objectMapper.writeValueAsString(customerDetails));
                        }
                    }

                    entitiesToSave.add(entity);
                }
            }

            motorOnSelectRepository.saveAll(entitiesToSave);
            log.info("Saved Motor Insurance On Select Details and Sending Acknowledgement");

            // Saving progress
            Optional<Progress> progressOpt = progressRepository.findByTransactionId(
                    motorOnselectRequest.getContext().getTransaction_id());
            if (progressOpt.isPresent()) {
                Progress progress = progressOpt.get();
                progress.setStatus(PreConstants.ON_SELECT);
                progress.setUpdatedAt(LocalDateTime.now());
                progressRepository.save(progress);
            }

            Acknowledgement acknowledgement = requestMapper.selectAckResponse(motorOnselectRequest);
            return ResponseEntity.ok(acknowledgement);

        } catch (Exception ex) {
            log.error("Error saving Motor OnSelect details: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error saving Motor OnSelect details.");
        }
    }

    /**
     * Process vehicle details and policy information from tags
     * Similar to processGeneralInformation in Health Insurance
     */
    private Map<String, Object> processVehicleDetails(List<MotorOnselectRequest.Message.Order.Item.Tag> tags) {
        Map<String, Object> detailsMap = new LinkedHashMap<>();

        if (tags != null) {
            for (MotorOnselectRequest.Message.Order.Item.Tag tag : tags) {
                if (tag.getDescriptor() != null) {
                    String tagCode = tag.getDescriptor().getCode();
                    Map<String, String> tagData = new LinkedHashMap<>();

                    if (tag.getList() != null) {
                        for (MotorOnselectRequest.Message.Order.Item.Tag.Value value : tag.getList()) {
                            if (value.getDescriptor() != null) {
                                String key = value.getDescriptor().getCode();
                                String val = value.getValue();
                                tagData.put(key, val);
                            }
                        }
                    }

                    detailsMap.put(tagCode, tagData);
                }
            }
        }

        return detailsMap;
    }

    @Override
    public ResponseEntity<?> getQuoteByTransactionId(String transactionId, String messageId) {

        MotorOnSelectEntity quoteDetails = motorOnSelectRepository
                .findByTransactionIdAndMessageId(transactionId, messageId);

        if (quoteDetails == null) {
            throw new TransactionIdNotFoundException("Motor Insurance Transaction ID not found");
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", quoteDetails.getId());
        response.put("itemId", quoteDetails.getItemId());
        response.put("transactionId", quoteDetails.getTransaction_id());
        response.put("messageId", quoteDetails.getMessageId());
        response.put("parentItemId", quoteDetails.getParentItemId());
        response.put("itemName", quoteDetails.getItemName());
        response.put("itemShortDesc", quoteDetails.getItemShortDesc());

        if (quoteDetails.getItemImages() != null) {
            response.put("itemImages", quoteDetails.getItemImages());
        }

        if (quoteDetails.getItemPrice() != null) {
            response.put("itemPrice", quoteDetails.getItemPrice());
            response.put("itemCurrency", quoteDetails.getItemCurrency());
        }

        if (quoteDetails.getVehicleDetails() != null) {
            response.put("vehicleDetails", quoteDetails.getVehicleDetails());
        }

        response.put("timeDuration", quoteDetails.getTimeDuration());
        response.put("timeLabel", quoteDetails.getTimeLabel());

        // XInput details
        response.put("xinputRequired", quoteDetails.getXinputRequired());
        response.put("xinputHeadName", quoteDetails.getXinputHeadName());
        if (quoteDetails.getXinputHeadings() != null) {
            response.put("xinputHeadings", quoteDetails.getXinputHeadings());
        }

        response.put("formId", quoteDetails.getFormId());
//        response.put("formUrl", quoteDetails.getFormUrl());
        String dynamicFormUrl = null;
        if (quoteDetails.getFormUrl() != null) {
            dynamicFormUrl =
                    bapUrl + "form-url"
                            + "?transactionId=" + quoteDetails.getTransaction_id()
                            + "&providerId=" + quoteDetails.getProviderId()
                            + "&formUrl=" + quoteDetails.getFormUrl()
                            + "&messageId=" + quoteDetails.getMessageId()
                            + "&formId=" + quoteDetails.getFormId();
        }
        response.put("formUrl", dynamicFormUrl);

        response.put("formMimeType", quoteDetails.getFormMimeType());
        response.put("formSubmissionId", quoteDetails.getFormSubmissionId());
        response.put("formResubmit", quoteDetails.getFormResubmit());
        response.put("formMultipleSubmissions", quoteDetails.getFormMultipleSubmissions());

        if (quoteDetails.getAddOns() != null) {
            response.put("addOns", quoteDetails.getAddOns());
        }

        response.put("quoteId", quoteDetails.getQuoteId());

        if (quoteDetails.getBreakupDetails() != null) {
            response.put("breakupDetails", quoteDetails.getBreakupDetails());
        }

        response.put("totalPrice", quoteDetails.getTotalPrice());
        response.put("currency", quoteDetails.getCurrency());
        response.put("ttl", quoteDetails.getTtl());

        response.put("providerId", quoteDetails.getProviderId());
        response.put("providerName", quoteDetails.getProviderName());
        response.put("providerShortDesc", quoteDetails.getProviderShortDesc());
        response.put("providerLongDesc", quoteDetails.getProviderLongDesc());

        if (quoteDetails.getProviderImages() != null) {
            response.put("providerImages", quoteDetails.getProviderImages());
        }

        if (quoteDetails.getFulfillmentId() != null) {
            response.put("fulfillmentId", quoteDetails.getFulfillmentId());
            response.put("fulfillmentType", quoteDetails.getFulfillmentType());
            response.put("fulfillmentState", quoteDetails.getFulfillmentState());
        }

        if (quoteDetails.getCustomerDetails() != null) {
            response.put("customerDetails", quoteDetails.getCustomerDetails());
        }

        if (quoteDetails.getPaymentUrl() != null) {
            response.put("paymentUrl", quoteDetails.getPaymentUrl());
        }

        return ResponseEntity.ok(response);
    }
}