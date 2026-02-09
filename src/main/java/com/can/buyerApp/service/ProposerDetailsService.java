package com.can.buyerApp.service;


import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface ProposerDetailsService {

    public ResponseEntity<?> getProposerTransactionId(String transactionId,String messageId);

    Map<String,String> getForm(String transactionId, String formUrl);
}
