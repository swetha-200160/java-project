package com.can.buyerApp.service.impl;

import com.can.buyerApp.dto.Acknowledgement;
import com.can.buyerApp.dto.FormStatusResponseDTO;
import com.can.buyerApp.dto.PaymentResponseDTO;
import com.can.buyerApp.entity.*;
import com.can.buyerApp.repository.*;
import com.can.buyerApp.request.MotorOnStatusRequest;

import com.can.buyerApp.service.OnStatusService;
import com.can.buyerApp.mapper.RequestMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;


@Slf4j
@Service
public class OnStatusServiceImpl implements OnStatusService {

    private final FormStatusRepository formStatusRepository;
    private final MotorPaymentRepository paymentDetailsRepository;
    private final RequestMapper requestMapper;
    public OnStatusServiceImpl(FormStatusRepository formStatusRepository, MotorPaymentRepository paymentDetailsRepository, RequestMapper requestMapper) {
        this.formStatusRepository = formStatusRepository;
        this.paymentDetailsRepository = paymentDetailsRepository;
        this.requestMapper = requestMapper;
    }

//    @Override
//    public ResponseEntity<?> saveOnStatusRequest(MotorOnStatusRequest req) {
//
//        String transactionId = req.getContext().getTransaction_id();
//
//        MotorOnStatusRequest.Order order = req.getMessage().getOrder();
//        MotorOnStatusRequest.Item item = order.getItems().get(0);
//
//        String providerId = order.getProvider().getId();
//        String providerName = order.getProvider().getDescriptor().getName();
//        String productName = item.getDescriptor().getName();
//
//        String formId = item.getXinput().getForm().getId();
//        String submissionId = item.getXinput().getForm_response().getSubmission_id();
//        String status = item.getXinput().getForm_response().getStatus();
//
//        log.info(
//                "Form on_status | tx={} | submissionId={} | status={}",
//                transactionId, submissionId, status
//        );
//        FormStatus entity = formStatusRepository
//                .findByTransactionIdAndFormId(transactionId, formId)
//                .orElseGet(FormStatus::new);
//
//        entity.setTransactionId(transactionId);
//        entity.setProviderId(providerId);
//        entity.setProviderName(providerName);
//        entity.setProductName(productName);
//        entity.setFormId(formId);
//        entity.setSubmissionId(submissionId);
//        entity.setStatus(status);
//        entity.setUpdatedAt(LocalDateTime.now());
//
//        if (entity.getId() == null) {
//            entity.setCreatedAt(LocalDateTime.now());
//            log.info("Creating new form_status row");
//        } else {
//            log.info("Updating existing form_status row");
//        }
//
//        formStatusRepository.save(entity);
//        log.info(
//                "Saved form_status | tx={} | submissionId={} | status={}",
//                transactionId, submissionId, status
//        );
//
//        Acknowledgement ack =
//                requestMapper.OnStatusAckResponse(req);
//        log.info(
//                "📤 Sending ACK for on_status | tx={} | messageId={}",
//                transactionId, req.getContext().getMessage_id()
//        );
//        return ResponseEntity.ok(ack);
//    }


    @Override
    public ResponseEntity<?> saveOnStatusRequest(MotorOnStatusRequest req) {

        String transactionId = req.getContext().getTransaction_id();

        log.info("🔔 on_status received | tx={}", transactionId);

        boolean hasPayment =
                req.getMessage() != null
                        && req.getMessage().getOrder() != null
                        && req.getMessage().getOrder().getPayments() != null
                        && !req.getMessage().getOrder().getPayments().isEmpty();

        if (!hasPayment) {
            log.info(" Payment NULL | saving FormStatus only | tx={}", transactionId);
            saveOrUpdateFormStatus(req);
        } else {
            log.info("Payment present | saving MotorPaymentDetails only | tx={}", transactionId);
            saveOrUpdatePaymentDetails(req);
        }

        Acknowledgement ack = requestMapper.OnStatusAckResponse(req);
        return ResponseEntity.ok(ack);
    }


    private void saveOrUpdateFormStatus(MotorOnStatusRequest req) {

        String transactionId = req.getContext().getTransaction_id();

        MotorOnStatusRequest.Order order = req.getMessage().getOrder();
        MotorOnStatusRequest.Item item = order.getItems().get(0);

        String providerId = order.getProvider().getId();
        String providerName = order.getProvider().getDescriptor().getName();
        String productName = item.getDescriptor().getName();

        String formId = item.getXinput().getForm().getId();
        String submissionId = item.getXinput().getForm_response().getSubmission_id();
        String status = item.getXinput().getForm_response().getStatus();

        FormStatus entity = formStatusRepository
                .findByTransactionIdAndFormId(transactionId, formId)
                .orElseGet(FormStatus::new);

        entity.setTransactionId(transactionId);
        entity.setProviderId(providerId);
        entity.setProviderName(providerName);
        entity.setProductName(productName);
        entity.setFormId(formId);
        entity.setSubmissionId(submissionId);
        entity.setStatus(status);
        entity.setUpdatedAt(LocalDateTime.now());

        if (entity.getId() == null) {
            entity.setCreatedAt(LocalDateTime.now());
            log.info(" Creating form_status | tx={} | formId={}", transactionId, formId);
        } else {
            log.info("Updating form_status | tx={} | formId={}", transactionId, formId);
        }

        formStatusRepository.save(entity);
    }



    private void saveOrUpdatePaymentDetails(MotorOnStatusRequest req) {

        String transactionId = req.getContext().getTransaction_id();
        String messageId = req.getContext().getMessage_id();

        MotorOnStatusRequest.Order order = req.getMessage().getOrder();

        if (order.getPayments() == null || order.getPayments().isEmpty()) {
            log.info("ℹ No payment data | tx={}", transactionId);
            return;
        }

        MotorOnStatusRequest.Payment payment = order.getPayments().get(0);
        MotorOnStatusRequest.PaymentParams params = payment.getParams();

        // 🔹 Customer (from fulfillment)
        String customerName = null;
        String customerEmail = null;
        String customerPhone = null;

        if (order.getFulfillments() != null && !order.getFulfillments().isEmpty()) {
            MotorOnStatusRequest.Fulfillment fulfillment = order.getFulfillments().get(0);

            if (fulfillment.getCustomer() != null) {

                if (fulfillment.getCustomer().getPerson() != null) {
                    customerName = fulfillment.getCustomer().getPerson().getName();
                }

                if (fulfillment.getCustomer().getContact() != null) {
                    customerEmail = fulfillment.getCustomer().getContact().getEmail();
                    customerPhone = fulfillment.getCustomer().getContact().getPhone();
                }
            }
        }

        MotorPaymentDetails entity = paymentDetailsRepository
                .findByTransactionId(transactionId)
                .orElseGet(MotorPaymentDetails::new);

        entity.setTransactionId(transactionId);
        entity.setMessageId(messageId);
        entity.setType(payment.getType());
        entity.setStatus(payment.getStatus());
        entity.setCollectedBy(payment.getCollected_by());

        if (params != null) {
            entity.setAmount(params.getAmount());
            entity.setBankAccountNumber(params.getBank_account_number());
            entity.setBankCode(params.getBank_code());
            entity.setCurrency(params.getCurrency());
        }

        //  SET CUSTOMER DATA
        entity.setCustomerName(customerName);
        entity.setCustomerEmail(customerEmail);
        entity.setCustomerPhone(customerPhone);

        entity.setUpdatedAt(LocalDateTime.now());

        if (entity.getId() == null) {
            entity.setCreatedAt(LocalDateTime.now());
            log.info("Creating payment_details | tx={}", transactionId);
        } else {
            log.info("♻️ Updating payment_details | tx={}", transactionId);
        }

        paymentDetailsRepository.save(entity);

        log.info(
                "Saved payment_details | tx={} | customer={} | amount={}",
                transactionId,
                customerName,
                entity.getAmount()
        );
    }






    @Override
    public ResponseEntity<?> getClaimStatusByTransactionId(String transactionId) {
        return null;
    }

    @Override
    public ResponseEntity<?> getRenewStatusByTransactionId(String transactionId) {
        return null;
    }

    @Override
    public ResponseEntity<?> getCancelStatusByTransactionId(String transactionId) {
        return null;
    }
    @Override
    public ResponseEntity<?> findByTransactionIdAndFormId(
            String transactionId,
            String formId) {

        log.info(
                "Fetching form_status | transactionId={} | formId={}",
                transactionId, formId
        );

        Optional<FormStatus> optionalEntity =
                formStatusRepository.findByTransactionIdAndFormId(
                        transactionId, formId
                );

        if (optionalEntity.isPresent()) {

            FormStatus entity = optionalEntity.get();

            FormStatusResponseDTO dto = new FormStatusResponseDTO();
            dto.setTransactionId(entity.getTransactionId());
            dto.setProviderId(entity.getProviderId());
            dto.setProviderName(entity.getProviderName());
            dto.setProductName(entity.getProductName());
            dto.setFormId(entity.getFormId());
            dto.setSubmissionId(entity.getSubmissionId());
            dto.setStatus(entity.getStatus());
            dto.setCreatedAt(entity.getCreatedAt());
            dto.setUpdatedAt(entity.getUpdatedAt());

            log.info(
                    "Form status found | transactionId={} | formId={} | status={}",
                    transactionId, formId, entity.getStatus()
            );

            return ResponseEntity.ok(dto);
        }

        log.warn(
                "Form status NOT found | transactionId={} | formId={}",
                transactionId, formId
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("Form status not found");
    }


    public ResponseEntity<?> getPaymentByTransactionId(String transactionId) {

        log.info("🔍 Fetching payment status | transactionId={}", transactionId);

        Optional<MotorPaymentDetails> optional =
                paymentDetailsRepository
                        .findTopByTransactionIdOrderByUpdatedAtDesc(transactionId);

        if (optional.isEmpty()) {
            log.warn("❌ Payment NOT found | transactionId={}", transactionId);

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Payment not found for transactionId");
        }

        MotorPaymentDetails payment = optional.get();

        log.info(
                "✅ Payment found | transactionId={} | status={} | amount={} {}",
                transactionId,
                payment.getStatus(),
                payment.getAmount(),
                payment.getCurrency()
        );

        PaymentResponseDTO dto = new PaymentResponseDTO();
        dto.setTransactionId(payment.getTransactionId());
        dto.setStatus(payment.getStatus());
        dto.setType(payment.getType());
        dto.setAmount(payment.getAmount());
        dto.setCurrency(payment.getCurrency());
        dto.setCollectedBy(payment.getCollectedBy());
        dto.setCustomerName(payment.getCustomerName());
        dto.setCustomerEmail(payment.getCustomerEmail());
        dto.setCustomerPhone(payment.getCustomerPhone());

        log.info(
                "📤 Returning payment response | transactionId={} | customer={}",
                transactionId,
                payment.getCustomerName()
        );

        return ResponseEntity.ok(dto);
    }



}

