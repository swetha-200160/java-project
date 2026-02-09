package com.can.buyerApp.service;

import com.can.buyerApp.request.MotorConfirmRequest;
import org.springframework.http.ResponseEntity;

public interface ConfirmService {

    ResponseEntity<?> sendConfirmRequest(String domain, String transactionId, String submissionId,String formId,String formStatus,String messageId);
    MotorConfirmRequest confirmRequest(String domain, String transactionId, String submissionId, String formId, String formStatus, String messageId);
}
