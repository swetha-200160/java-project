package com.can.buyerApp.service;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface NomineeDetailsService {
    public ResponseEntity<?> getNomineeTransactionId(String transactionId, String messageId);

    Map<String,String> getForm(String transactionId, String formUrl);
}
