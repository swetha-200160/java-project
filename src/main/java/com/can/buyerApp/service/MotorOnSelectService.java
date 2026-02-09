package com.can.buyerApp.service;


import com.can.buyerApp.request.MotorOnselectRequest;
import org.springframework.http.ResponseEntity;

public interface MotorOnSelectService {

    ResponseEntity<?> saveSelectRequest(MotorOnselectRequest motorOnselectRequest);
    
    ResponseEntity<?> getQuoteByTransactionId(String transactionId, String messageId);
}