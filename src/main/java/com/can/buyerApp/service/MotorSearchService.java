package com.can.buyerApp.service;

import com.can.buyerApp.request.MotorSearchRequest;
import org.springframework.http.ResponseEntity;

public interface MotorSearchService {

    ResponseEntity<?> sendMotorSearchRequest(String domain, String type, Long userId, String agentId);
    

    MotorSearchRequest createMotorSearchRequest(String domain, String type, Long userId, String agentId);


    ResponseEntity<?> sendSecondMotorSearchRequest(String domain, String type,
                                                   String transactionId, String messageId,
                                                   String submissionId,
                                                   String providerId, String formStatus,
                                                   String formId,
                                                   String categoryId);

    MotorSearchRequest createSecondMotorSearchRequest(String domain, String type,
                                                             String transactionId, String messageId,
                                                             String submissionId, String providerId,
                                                             String formStatus, String formId, String categoryId);
}