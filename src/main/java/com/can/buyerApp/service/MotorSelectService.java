package com.can.buyerApp.service;


import com.can.buyerApp.request.MotorSelectRequest;
import org.springframework.http.ResponseEntity;
import java.util.List;

public interface MotorSelectService {

    ResponseEntity<?> sendSelectRequest(String domain, String transactionId, List<String> addons,
                                        String itemId, String formStatus,
                                        String formId, String submissionId);

    MotorSelectRequest createSelectRequest(String domain, String transactionId, List<String> addons,
                                           String itemId, String formStatus,
                                           String formId, String submissionId);
}