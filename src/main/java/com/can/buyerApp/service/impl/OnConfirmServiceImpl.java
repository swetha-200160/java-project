package com.can.buyerApp.service.impl;

import com.can.buyerApp.constants.PreConstants;
import com.can.buyerApp.dto.PolicyDocumentsDTO;
import com.can.buyerApp.entity.MotorPolicyDocuments;
import com.can.buyerApp.exception.TransactionIdNotFoundException;
import com.can.buyerApp.mapper.RequestMapper;
import com.can.buyerApp.repository.MotorDocumentRepository;
import com.can.buyerApp.repository.ProgressRepository;
import com.can.buyerApp.request.MotorOnConfirmRequest;
import com.can.buyerApp.service.OnConfirmService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class OnConfirmServiceImpl implements OnConfirmService {

    private final MotorDocumentRepository documentRepository;
    private  final RequestMapper requestMapper;
    private  final ProgressRepository progressRepository;

    public OnConfirmServiceImpl(MotorDocumentRepository documentRepository, RequestMapper requestMapper, ProgressRepository progressRepository) {
        this.documentRepository = documentRepository;
        this.requestMapper = requestMapper;
        this.progressRepository = progressRepository;
    }


    @Override
    public ResponseEntity<?> saveConfirmRequest(MotorOnConfirmRequest onConfirmRequest) {

        log.info(
                "ON_CONFIRM received | txnId={}",
                onConfirmRequest.getContext().getTransaction_id()
        );

        var context = onConfirmRequest.getContext();
        var order = onConfirmRequest.getMessage().getOrder();

        var payment = order.getPayments().get(0);
        var fulfillment = order.getFulfillments().isEmpty()
                ? null
                : order.getFulfillments().get(0);

        // -------- SAVE DOCUMENTS --------
        order.getDocuments().forEach(doc -> {

            MotorPolicyDocuments policyDocuments = new MotorPolicyDocuments();

            // Context
            policyDocuments.setTransactionId(context.getTransaction_id());
            policyDocuments.setMessageId(context.getMessage_id());

            // Document
            policyDocuments.setDocumentType(doc.getDescriptor().getCode());
            policyDocuments.setCode(doc.getDescriptor().getCode());
            policyDocuments.setName(doc.getDescriptor().getName());
            policyDocuments.setShortDesc(doc.getDescriptor().getShort_desc());
            policyDocuments.setUrl(doc.getUrl());
            policyDocuments.setMimeType(doc.getMime_type());

            // Policy
            policyDocuments.setOrderId(order.getId());
            policyDocuments.setOrderStatus(order.getStatus());

            // Provider
            policyDocuments.setProviderId(order.getProvider().getId());
            policyDocuments.setProviderName(order.getProvider().getDescriptor().getName());

            // Payment
            policyDocuments.setAmount(payment.getParams().getAmount());
            policyDocuments.setCurrency(payment.getParams().getCurrency());
            policyDocuments.setPaymentStatus(payment.getStatus());
            policyDocuments.setPaymentTransactionId(payment.getParams().getTransaction_id());

            // Customer & Fulfillment (PERSON.NAME INCLUDED )
            if (fulfillment != null) {
                policyDocuments.setCustomerName(
                        fulfillment.getCustomer().getPerson().getName()
                );
                policyDocuments.setEmail(
                        fulfillment.getCustomer().getContact().getEmail()
                );
                policyDocuments.setPhoneNumber(
                        fulfillment.getCustomer().getContact().getPhone()
                );
                policyDocuments.setFulfillmentId(fulfillment.getId());
                policyDocuments.setFulfillmentType(fulfillment.getType());
                policyDocuments.setFulfillmentState(
                        fulfillment.getState().getDescriptor().getCode()
                );
            }


            policyDocuments.setCreatedAt(LocalDateTime.now());
            policyDocuments.setUpdatedAt(LocalDateTime.now());

            documentRepository.save(policyDocuments);

            log.debug(
                    "Policy document saved | txnId={} | policyId={} | docCode={}",
                    context.getTransaction_id(),
                    order.getId(),
                    doc.getDescriptor().getCode()
            );
        });

        // -------- PROGRESS UPDATE --------
        progressRepository.findByTransactionId(context.getTransaction_id())
                .ifPresentOrElse(progress -> {
                    progress.setStatus(PreConstants.ON_CONFIRM);
                    progress.setUpdatedAt(LocalDateTime.now());
                    progressRepository.save(progress);

                    log.info(
                            "Progress updated | txnId={} | status={}",
                            context.getTransaction_id(),
                            PreConstants.ON_CONFIRM
                    );
                }, () -> {
                    log.warn(
                            "Progress not found | txnId={} | skipping progress update",
                            context.getTransaction_id()
                    );
                });

        return ResponseEntity.ok(
                requestMapper.confirmAckResponse(onConfirmRequest)
        );
    }


    @Override
    public List<PolicyDocumentsDTO> getPolicyDocuments(String transactionId) {

        List<MotorPolicyDocuments> documents =
                documentRepository.findByTransactionIdAndIsLatestTrue(transactionId);

        if (documents.isEmpty()) {
            throw new TransactionIdNotFoundException(
                    "No documents found for transactionId: " + transactionId
            );
        }

        return documents.stream().map(document -> {

            PolicyDocumentsDTO dto = new PolicyDocumentsDTO();

            dto.setId(document.getId());
            dto.setTransactionId(document.getTransactionId());
            dto.setMessageId(document.getMessageId());

            // Document
            dto.setDocumentType(document.getDocumentType());
            dto.setCode(document.getCode());
            dto.setName(document.getName());
            dto.setShortDesc(document.getShortDesc());
            dto.setUrl(document.getUrl());
            dto.setMimeType(document.getMimeType());

            //
            dto.setPolicyInfo(document.getPolicyInfo());

            // Policy
            dto.setPolicyId(document.getOrderId());
            dto.setPolicyStatus(document.getOrderStatus());

            // Provider
            dto.setProviderId(document.getProviderId());
            dto.setProviderName(document.getProviderName());

            // Customer
            dto.setCustomerName(document.getCustomerName());
            dto.setEmail(document.getEmail());
            dto.setPhoneNumber(document.getPhoneNumber());

            // Payment
            dto.setAmount(document.getAmount());
            dto.setCurrency(document.getCurrency());
            dto.setPaymentStatus(document.getPaymentStatus());
            dto.setPaymentTransactionId(document.getPaymentTransactionId());

            // Fulfillment
            dto.setFulfillmentId(document.getFulfillmentId());
            dto.setFulfillmentType(document.getFulfillmentType());
            dto.setFulfillmentState(document.getFulfillmentState());

            return dto;
        }).toList();
    }



}
