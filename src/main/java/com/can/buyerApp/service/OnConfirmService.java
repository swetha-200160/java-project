package com.can.buyerApp.service;

import com.can.buyerApp.dto.PolicyDocumentsDTO;
import com.can.buyerApp.request.MotorOnConfirmRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface OnConfirmService {

    ResponseEntity<?> saveConfirmRequest(MotorOnConfirmRequest onConfirmRequest);
    List<PolicyDocumentsDTO> getPolicyDocuments(String transactionId);

}
