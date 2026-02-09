package com.can.buyerApp.service;

import com.can.buyerApp.request.InitRequest;
import com.can.buyerApp.request.SecondInitRequest;
import org.springframework.http.ResponseEntity;

public interface MotorInitService {
    ResponseEntity<?> sendInitRequest(String domain, String transactionId, String submissionId,String formStatus,String messageId);

    public InitRequest createFirstInitRequest(String domain, String transactionId, String submissionId, String msgId, String formStatus);

    public SecondInitRequest createSecondInitRequest(
            String domain,
            String transactionId,
            String submissionId,
            String formStatus,
            String messageId
    );

    }
