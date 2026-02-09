package com.can.buyerApp.service;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface FormService {
   // ResponseEntity<?> submitFormData(Map<String, String> formData, String url);

    ResponseEntity<?> submitMotorVehicleForm(
            Map<String, String> formData,
            String formUrl,
            String transactionId,
            String messageId
    );

    ResponseEntity<?> submitMotorManualReviewForm(
            Map<String, String> formData,
            String formUrl,
            String transactionId,
            String messageId
    );

    ResponseEntity<?> submitPanDobForm(
            Map<String, String> formData,
            String formUrl,
            String transactionId,
            String messageId
    );


    ResponseEntity<?> submitPersonalDetailsForm(Map<String, String> formData, String formUrl, String transactionId,String messageId);

    ResponseEntity<?> submitProposerFormData(Map<String, String> formData, String formUrl,String transactionId, String messageId);

    ResponseEntity<?> submitNomineeFormData(Map<String, String> formData, String formUrl,String transactionId, String messageId);

    ResponseEntity<String> saveFamilyData(Map<String, Object> formData, String formUrl, String transactionId, String messageId);

    ResponseEntity<?> submitVehicleInformationForm(Map<String, String> formData, String formUrl, String transactionId, String messageId);
}
