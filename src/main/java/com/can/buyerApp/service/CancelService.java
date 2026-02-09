package com.can.buyerApp.service;

import org.springframework.http.ResponseEntity;

public interface CancelService {
    ResponseEntity<?> sendCancelRequest(String domain, String type, String transactionId, Long cancellationReasonId, String orderId, String description);

}
