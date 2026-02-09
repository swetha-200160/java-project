package com.can.buyerApp.service;



import com.can.buyerApp.request.MotorOnStatusRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;

public interface OnStatusService {

    ResponseEntity<?> saveOnStatusRequest(MotorOnStatusRequest onStatusRequest) throws JsonProcessingException;

    ResponseEntity<?> getClaimStatusByTransactionId(String transactionId);

    ResponseEntity<?> getRenewStatusByTransactionId(String transactionId);

    ResponseEntity<?> getCancelStatusByTransactionId(String transactionId);



    ResponseEntity<?> findByTransactionIdAndFormId(String transactionId, String formId);

    ResponseEntity<?> getPaymentByTransactionId(String transactionId);
}
