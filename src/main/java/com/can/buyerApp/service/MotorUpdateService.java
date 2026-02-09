package com.can.buyerApp.service;

import org.springframework.http.ResponseEntity;

public interface MotorUpdateService {
    ResponseEntity<?> sendUpdateRequest(String domain, String transactionId, String phoneNumber);
}
