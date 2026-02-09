package com.can.buyerApp.service.impl;

import com.can.buyerApp.dto.Acknowledgement;
import com.can.buyerApp.entity.*;
import com.can.buyerApp.mapper.RequestMapper;
import com.can.buyerApp.repository.*;
import com.can.buyerApp.request.OnUpdateRequest;
import com.can.buyerApp.service.MotorOnUpdateService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MotorOnUpdateServiceImpl implements MotorOnUpdateService {


    private final ClaimRepository claimRepository;
    private final RenewRepository renewRepository;
    private final CancelRepository cancelRepository;
    private final RequestMapper requestMapper;
    private final ContextRepository contextRepository;
    private final MotorDocumentRepository documentRepository;
    public MotorOnUpdateServiceImpl(ClaimRepository claimRepository, RenewRepository renewRepository, CancelRepository cancelRepository, RequestMapper requestMapper, ContextRepository contextRepository, MotorDocumentRepository documentRepository) {
        this.claimRepository = claimRepository;
        this.renewRepository = renewRepository;
        this.cancelRepository = cancelRepository;
        this.requestMapper = requestMapper;
        this.contextRepository = contextRepository;
        this.documentRepository = documentRepository;

    }


//    @Override
//    public ResponseEntity<?> saveOnUpdate(OnUpdateRequest request)
//            throws JsonProcessingException {
//
//        log.info("Received ON_UPDATE | txnId={}",
//                request.getContext().getTransaction_id());
//
//        var order = request.getMessage().getOrder();
//
//        //  CANCELLATION
//        if ("CANCELLED".equalsIgnoreCase(order.getStatus())) {
//
//            log.info("Policy cancellation on_update received");
//            saveCancelOnUpdate(request);
//
//            return ResponseEntity.ok(
//                    requestMapper.onUpdateAckResponse(request)
//            );
//        }
//
//        //  PROCESS EACH FULFILLMENT
//        for (OnUpdateRequest.Fulfillment fulfillment : order.getFulfillments()) {
//
//            String type = fulfillment.getType();
//            String state = fulfillment.getState() != null
//                    ? fulfillment.getState().getDescriptor().getCode()
//                    : null;
//
//            // CLAIM UPDATE
//            if ("CLAIM".equalsIgnoreCase(type)) {
//
//                log.info("Claim on_update received | state={}", state);
//                saveClaimOnUpdate(request);
//            }
//
//            // POLICY UPDATE
//            else if ("POLICY".equalsIgnoreCase(type)) {
//
//                log.info("Policy on_update received | state={}", state);
//                savePolicyOnUpdate(request);
//            }
//
//        }
//        // ACK RESPONSE
//        return ResponseEntity.ok(
//                requestMapper.onUpdateAckResponse(request)
//        );
//    }


    @Override
    public ResponseEntity<?> saveOnUpdate(OnUpdateRequest request)
            throws JsonProcessingException {

        log.info("Received ON_UPDATE | txnId={}",
                request.getContext().getTransaction_id());

        //  Build ACK immediately
        Acknowledgement ack =
                requestMapper.onUpdateAckResponse(request);

        try {
            var order = request.getMessage().getOrder();

            // CANCELLATION
            if ("CANCELLED".equalsIgnoreCase(order.getStatus())) {
                saveCancelOnUpdate(request);
                return ResponseEntity.ok(ack);
            }

            // PROCESS FULFILLMENTS
            for (OnUpdateRequest.Fulfillment fulfillment : order.getFulfillments()) {

                String type = fulfillment.getType();

                if ("CLAIM".equalsIgnoreCase(type)) {
                    saveClaimOnUpdate(request);
                }
                else if ("POLICY".equalsIgnoreCase(type)) {
                    savePolicyOnUpdate(request);
                }
            }

        } catch (Exception e) {
            // NEVER let exception break ACK
            log.error("on_update business logic failed", e);
        }
        log.info("ON_UPDATE ACK OBJECT  = {}", ack);
        return ResponseEntity.ok(ack);
    }



    private void savePolicyOnUpdate(OnUpdateRequest request) {

        var context = request.getContext();
        var order = request.getMessage().getOrder();

        var fulfillment = order.getFulfillments().isEmpty()
                ? null
                : order.getFulfillments().get(0);

        var payment = order.getPayments().isEmpty()
                ? null
                : order.getPayments().get(0);

        // Save documents ONLY if present
        if (order.getDocuments() == null || order.getDocuments().isEmpty()) {
            return; // nothing to save
        }

        order.getDocuments().forEach(doc -> {

            MotorPolicyDocuments entity = new MotorPolicyDocuments();

            // Context
            entity.setTransactionId(context.getTransaction_id());
            entity.setMessageId(context.getMessage_id());

            // Document (FROM PAYLOAD)
            entity.setDocumentType(doc.getDescriptor().getCode());
            entity.setCode(doc.getDescriptor().getCode());
            entity.setName(doc.getDescriptor().getName());
            entity.setShortDesc(doc.getDescriptor().getShort_desc());
            entity.setUrl(doc.getUrl());
            entity.setMimeType(doc.getMime_type());

            // Order
            entity.setOrderId(order.getId());
            entity.setOrderStatus(order.getStatus());

            // Provider
            entity.setProviderId(order.getProvider().getId());
            entity.setProviderName(order.getProvider().getDescriptor().getName());

            // Payment
            if (payment != null) {
                entity.setAmount(payment.getParams().getAmount());
                entity.setCurrency(payment.getParams().getCurrency());
                entity.setPaymentStatus(payment.getStatus());
                entity.setPaymentTransactionId(payment.getParams().getTransaction_id());
            }

            // Fulfillment
            if (fulfillment != null) {
                entity.setFulfillmentId(fulfillment.getId());
                entity.setFulfillmentType(fulfillment.getType());
                entity.setFulfillmentState(
                        fulfillment.getState().getDescriptor().getCode()
                );

                if (fulfillment.getCustomer() != null) {
                    entity.setCustomerName(
                            fulfillment.getCustomer().getPerson().getName()
                    );
                    entity.setEmail(
                            fulfillment.getCustomer().getContact().getEmail()
                    );
                    entity.setPhoneNumber(
                            fulfillment.getCustomer().getContact().getPhone()
                    );
                }
            }

            entity.setCreatedAt(LocalDateTime.now());
            entity.setUpdatedAt(LocalDateTime.now());

            String policyInfo = null;
            try {
                policyInfo = extractPolicyInfoJson(request);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            if (policyInfo != null) {
                entity.setPolicyInfo(policyInfo);
            }

            String docType = doc.getDescriptor().getCode();

//            if ("POLICY_DOC".equalsIgnoreCase(docType)) {
//                documentRepository.updateLatestPolicyDoc(order.getId());
//                entity.setIsLatest(true);
//            } else {
//                entity.setIsLatest(true);
//            }

            if ("POLICY_DOC".equalsIgnoreCase(docType)) {
                updateLatestPolicyDoc(order.getId());
                entity.setIsLatest(true);
            } else {
                entity.setIsLatest(true);
            }

            documentRepository.save(entity);
        });
    }


    private void updateLatestPolicyDoc(String orderId) {

        List<MotorPolicyDocuments> policyDocs =
                documentRepository.findByOrderIdAndDocumentType(
                        orderId, "POLICY_DOC"
                );

        for (MotorPolicyDocuments doc : policyDocs) {
            doc.setIsLatest(false);
            doc.setUpdatedAt(LocalDateTime.now());
        }

        documentRepository.saveAll(policyDocs);
    }



    private String extractPolicyInfoJson(OnUpdateRequest request)
            throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();

        var items = request.getMessage().getOrder().getItems();
        if (items == null) return null;

        for (var item : items) {
            if (item.getTags() == null) continue;

            for (var tag : item.getTags()) {
                if ("POLICY_INFO".equalsIgnoreCase(
                        tag.getDescriptor().getCode())) {

                    return mapper.writeValueAsString(tag);
                }
            }
        }
        return null;
    }




    private void saveClaimOnUpdate(OnUpdateRequest onUpdateRequest) {
        ClaimDetails claimDetails=new ClaimDetails();
        log.info("Processing CLAIM: {}", onUpdateRequest);
        OnUpdateRequest.Document document = onUpdateRequest.getMessage().getOrder().getDocuments().get(1);

        onUpdateRequest.getMessage().getOrder().getFulfillments().stream()
                .forEach(fulfillment -> {
                    if (fulfillment.getTags() != null) {
                        ClaimDetails entity = new ClaimDetails();
                        entity.setPolicyId(onUpdateRequest.getMessage().getOrder().getId());
                        entity.setTransactionId(onUpdateRequest.getContext().getTransaction_id());
                        entity.setMessageId(onUpdateRequest.getContext().getMessage_id());
                        entity.setState(fulfillment.getState().getDescriptor().getCode());
                        entity.setType(fulfillment.getType());
                        entity.setName(document.getDescriptor().getName());
                        entity.setShort_desc(document.getDescriptor().getShort_desc());
                        entity.setUrl(document.getUrl());
                        Map<String, String> details = fulfillment.getTags().stream()
                                .flatMap(tag -> tag.getList().stream())
                                .filter(Objects::nonNull)
                                .collect(Collectors.toMap(
                                        listItem -> listItem.getDescriptor().getCode(),
                                        listItem -> listItem.getValue()
                                ));
                        entity.setClaimDetails(details);
                        entity.setUpdatedAt(LocalDateTime.now());
                        log.info("saving claim :{}", entity);
                        claimRepository.save(entity);
                        log.info("Saved Claim: {}", entity);
                    }
                });
    }


    private void saveRenewOnUpdate(OnUpdateRequest onUpdateRequest) {

        List<OnUpdateRequest.Fulfillment> fulfillments = onUpdateRequest.getMessage().getOrder().getFulfillments();
        List<OnUpdateRequest.Document> documents = onUpdateRequest.getMessage().getOrder().getDocuments();

                RenewDetails renewDetails = new RenewDetails();
                renewDetails.setPolicyId(onUpdateRequest.getMessage().getOrder().getId());
                renewDetails.setTransactionId(onUpdateRequest.getContext().getTransaction_id());
                renewDetails.setMessageId(onUpdateRequest.getContext().getMessage_id());
                renewDetails.setType(fulfillments.get(1).getType());
                renewDetails.setState(fulfillments.get(1).getState().getDescriptor().getCode());
                OnUpdateRequest.Document renewDocument = documents.get(2);
                renewDetails.setShort_desc(renewDocument.getDescriptor().getShort_desc());
                renewDetails.setName(renewDocument.getDescriptor().getName());
                renewDetails.setUrl(renewDocument.getUrl());
                renewDetails.setUpdatedAt(LocalDateTime.now());
                log.info("Saving RENEWAL:{}", renewDetails);
                renewRepository.save(renewDetails);


    }

    private void saveCancelOnUpdate(OnUpdateRequest onUpdateRequest) {

        OnUpdateRequest.Order order = onUpdateRequest.getMessage().getOrder();

        List<OnUpdateRequest.Item> items = order.getItems();
        List<CancelDetails> entitiesToSave = items.stream()
                .filter(Objects::nonNull)
                .map(item -> {
                    CancelDetails entity = new CancelDetails();
                    entity.setItemId(item.getId());
                    entity.setPolicyId(order.getId());
                    entity.setTransaction_id(onUpdateRequest.getContext().getTransaction_id());
                    entity.setMessageId(onUpdateRequest.getContext().getMessage_id());
                    entity.setQuoteId(order.getQuote().getId());
                    entity.setStatus(order.getStatus());
                    entity.setUpdatedAt(LocalDateTime.now());
                    return entity;
                })
                .collect(Collectors.toList());

        cancelRepository.saveAll(entitiesToSave);
    }




}