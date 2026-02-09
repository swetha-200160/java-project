package com.can.buyerApp.service.impl;

import com.can.buyerApp.constants.PreConstants;
import com.can.buyerApp.dto.Acknowledgement;
import com.can.buyerApp.entity.MotorOnSelectEntity;
import com.can.buyerApp.entity.Progress;
import com.can.buyerApp.mapper.RequestMapper;
import com.can.buyerApp.repository.MotorOnSelectRepository;
import com.can.buyerApp.repository.ProgressRepository;
import com.can.buyerApp.request.OnInitRequest;
import com.can.buyerApp.service.MotorOnInitService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class MotorOnInitServiceImpl implements MotorOnInitService {


    private final RequestMapper requestMapper;
    private final ProgressRepository progressRepository;
    private final ObjectMapper objectMapper;
    private final MotorOnSelectRepository motorOnSelectRepository;
    public MotorOnInitServiceImpl(RequestMapper requestMapper, ProgressRepository progressRepository, ObjectMapper objectMapper, MotorOnSelectRepository motorOnSelectRepository) {

        this.requestMapper = requestMapper;
        this.progressRepository = progressRepository;
        this.objectMapper = objectMapper;
        this.motorOnSelectRepository = motorOnSelectRepository;
    }

    @Override
    public ResponseEntity<?> saveOnInitRequest(OnInitRequest onInitRequest) {

        try {
            log.info("MOTOR_ON_INIT_RESPONSE: {}",
                    objectMapper.writeValueAsString(onInitRequest));

            OnInitRequest.Message.Order order =
                    onInitRequest.getMessage().getOrder();

            OnInitRequest.Message.Order.Item item =
                    order.getItems().get(0);

            OnInitRequest.Message.Order.Fulfillment fulfillment =
                    order.getFulfillments().get(0);

            MotorOnSelectEntity entity = new MotorOnSelectEntity();

            // ================= BASIC =================
            entity.setTransaction_id(onInitRequest.getContext().getTransaction_id());
            entity.setMessageId(onInitRequest.getContext().getMessage_id());
            entity.setItemId(item.getId());
            entity.setParentItemId(item.getParent_item_id());

            // ================= FULFILLMENT =================
            entity.setFulfillmentId(fulfillment.getId());
            entity.setFulfillmentType(fulfillment.getType());
            entity.setFulfillmentState(
                    fulfillment.getState().getDescriptor().getCode()
            );

            // ================= FULFILLMENT IDS =================
            entity.setFulfillmentIds(
                    objectMapper.writeValueAsString(item.getFulfillment_ids())
            );

            // ================= ITEM DETAILS =================
            if (item.getDescriptor() != null) {
                entity.setItemName(item.getDescriptor().getName());
                entity.setItemShortDesc(item.getDescriptor().getShort_desc());

                if (item.getDescriptor().getImages() != null) {
                    List<Map<String, String>> images = new ArrayList<>();
                    for (OnInitRequest.Message.Order.Item.Descriptor.Image img
                            : item.getDescriptor().getImages()) {
                        Map<String, String> map = new HashMap<>();
                        map.put("url", img.getUrl());
                        map.put("size_type", img.getSize_type());
                        images.add(map);
                    }
                    entity.setItemImages(objectMapper.writeValueAsString(images));
                }
            }

            // ================= PRICE =================
            if (item.getPrice() != null) {
                entity.setItemPrice(item.getPrice().getValue());
                entity.setItemCurrency(item.getPrice().getCurrency());
            }

            // ================= TIME =================
            if (item.getTime() != null) {
                entity.setTimeDuration(item.getTime().getDuration());
                entity.setTimeLabel(item.getTime().getLabel());
            }

            // ================= CUSTOMER (CONTACT + PERSON) =================
            Map<String, String> customerMap = new LinkedHashMap<>();

            if (fulfillment.getCustomer() != null) {

                if (fulfillment.getCustomer().getContact() != null) {
                    customerMap.put(
                            "email",
                            fulfillment.getCustomer().getContact().getEmail()
                    );
                    customerMap.put(
                            "phone",
                            fulfillment.getCustomer().getContact().getPhone()
                    );
                }

                if (fulfillment.getCustomer().getPerson() != null) {
                    customerMap.put(
                            "name",
                            fulfillment.getCustomer().getPerson().getName()
                    );
                }
            }

            entity.setCustomerDetails(
                    objectMapper.writeValueAsString(customerMap)
            );

            // ================= ADD ONS =================
            if (item.getAdd_ons() != null) {
                List<Map<String, Object>> addonList = new ArrayList<>();

                for (OnInitRequest.Message.Order.Item.AddOn addOn : item.getAdd_ons()) {
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("addonId", addOn.getId());
                    map.put("addonName", addOn.getDescriptor().getName());
                    map.put("addonCode", addOn.getDescriptor().getCode());
                    map.put("priceValue", addOn.getPrice().getValue());
                    map.put("currency", addOn.getPrice().getCurrency());
                    map.put("selectedCount",
                            addOn.getQuantity().getSelected().getCount());
                    addonList.add(map);
                }

                entity.setAddOns(objectMapper.writeValueAsString(addonList));
            }

            // ================= XINPUT (ALL FIELDS) =================
            if (item.getXinput() != null) {

                entity.setXinputRequired(item.getXinput().isRequired());

                if (item.getXinput().getHead() != null) {

                    if (item.getXinput().getHead().getDescriptor() != null) {
                        entity.setXinputHeadName(
                                item.getXinput().getHead()
                                        .getDescriptor()
                                        .getName()
                        );
                    }

                    if (item.getXinput().getHead().getHeadings() != null) {
                        entity.setXinputHeadings(
                                objectMapper.writeValueAsString(
                                        item.getXinput()
                                                .getHead()
                                                .getHeadings()
                                )
                        );
                    }
                }

                if (item.getXinput().getForm() != null) {
                    entity.setFormId(item.getXinput().getForm().getId());
                    entity.setFormUrl(item.getXinput().getForm().getUrl());
                    entity.setFormMimeType(item.getXinput().getForm().getMime_type());
                    entity.setFormResubmit(item.getXinput().getForm().isResubmit());
                    entity.setFormMultipleSubmissions(
                            item.getXinput().getForm().isMultiple_sumbissions()
                    );
                }
            }

            // ================= VEHICLE DETAILS (TAGS) =================
            Map<String, Object> vehicleDetails = new LinkedHashMap<>();

            if (item.getTags() != null) {
                for (OnInitRequest.Message.Order.Item.Tag tag : item.getTags()) {

                    Map<String, String> tagValues = new LinkedHashMap<>();

                    if (tag.getList() != null) {
                        for (OnInitRequest.Message.Order.Item.Tag.Value val
                                : tag.getList()) {
                            tagValues.put(
                                    val.getDescriptor().getCode(),
                                    val.getValue()
                            );
                        }
                    }

                    vehicleDetails.put(
                            tag.getDescriptor().getCode(),
                            tagValues
                    );
                }
            }

            entity.setVehicleDetails(
                    objectMapper.writeValueAsString(vehicleDetails)
            );

            // ================= PROVIDER =================
            if (order.getProvider() != null) {
                entity.setProviderId(order.getProvider().getId());

                if (order.getProvider().getDescriptor() != null) {
                    entity.setProviderName(
                            order.getProvider().getDescriptor().getName()
                    );
                    entity.setProviderShortDesc(
                            order.getProvider().getDescriptor().getShort_desc()
                    );
                    entity.setProviderLongDesc(
                            order.getProvider().getDescriptor().getLong_desc()
                    );

                    if (order.getProvider().getDescriptor().getImages() != null) {
                        entity.setProviderImages(
                                objectMapper.writeValueAsString(
                                        order.getProvider()
                                                .getDescriptor()
                                                .getImages()
                                )
                        );
                    }
                }
            }

            // ================= QUOTE & BREAKUP =================
            if (order.getQuote() != null) {

                entity.setQuoteId(order.getQuote().getId());
                entity.setTotalPrice(order.getQuote().getPrice().getValue());
                entity.setCurrency(order.getQuote().getPrice().getCurrency());
                entity.setTtl(order.getQuote().getTtl());

                Map<String, Object> breakupMap = new LinkedHashMap<>();

                for (OnInitRequest.Message.Order.Quote.Breakup breakup
                        : order.getQuote().getBreakup()) {

                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("value", breakup.getPrice().getValue());
                    map.put("currency", breakup.getPrice().getCurrency());

                    if (breakup.getItem() != null) {
                        map.put("item",
                                Map.of("id", breakup.getItem().getId()));
                    }

                    breakupMap.put(breakup.getTitle(), map);
                }

                entity.setBreakupDetails(
                        objectMapper.writeValueAsString(breakupMap)
                );
            }

            // ================= PAYMENT URL =================
            if (order.getPayments() != null && !order.getPayments().isEmpty()) {
                entity.setPaymentUrl(order.getPayments().get(0).getUrl());
            }

            entity.setCreatedAt(LocalDateTime.now());
            entity.setUpdatedAt(LocalDateTime.now());

            // ================= INSERT =================
            motorOnSelectRepository.save(entity);
            // Saving progress
            Optional<Progress> progressOpt = progressRepository.findByTransactionId(
                    onInitRequest.getContext().getTransaction_id());
            if (progressOpt.isPresent()) {
                Progress progress = progressOpt.get();
                progress.setStatus(PreConstants.INIT);
                progress.setUpdatedAt(LocalDateTime.now());
                progressRepository.save(progress);
            }

            Acknowledgement acknowledgement = requestMapper.initAckResponse(onInitRequest);
            return ResponseEntity.ok(acknowledgement);

        } catch (Exception e) {
            log.error("Error saving ON_INIT", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to save ON_INIT");
        }
    }



}
