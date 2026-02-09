package com.can.buyerApp.service;


import com.can.buyerApp.request.MotorStatusRequest;
import org.springframework.http.ResponseEntity;

public interface StatusService {

    ResponseEntity<?> sendStatusRequest(String domain, String transactionId);
    MotorStatusRequest createStatusRequest(String domain, String transactionId) ;
}
