package com.can.buyerApp.controller;

import com.can.buyerApp.service.FormService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@Slf4j
@RestController
public class FormController {

    @Autowired
    private FormService formService;

    @PostMapping("/vehicle-form")
    public ResponseEntity<?> submitVehicleForm(
            @RequestBody Map<String, String> formData,
            @RequestParam String formUrl,
            @RequestParam String transactionId,
            @RequestParam String messageId) {

        log.info("Received motor vehicle form. TransactionId={}, MessageId={}",
                transactionId, messageId);

        return formService.submitMotorVehicleForm(
                formData,
                formUrl,
                transactionId,
                messageId
        );
    }

    @PostMapping("/manual-review-form")
    public ResponseEntity<?> submitManualReviewForm(
            @RequestBody Map<String, String> formData,
            @RequestParam String formUrl,
            @RequestParam String transactionId,
            @RequestParam String messageId) {

        log.info("Motor manual review form received. TxnId={}, MsgId={}",
                transactionId, messageId);

        return formService.submitMotorManualReviewForm(
                formData,
                formUrl,
                transactionId,
                messageId
        );
    }

    @PostMapping("/pan-dob")
    public ResponseEntity<?> submitPanDobForm(
            @RequestBody Map<String, String> formData,
            @RequestParam String formUrl,
            @RequestParam String transactionId,
            @RequestParam String messageId) {

        log.info("Received PAN-DOB form. TxnId={}, MsgId={}",
                transactionId, messageId);

        return formService.submitPanDobForm(
                formData,
                formUrl,
                transactionId,
                messageId
        );
    }


    @PostMapping("/vehicle-information-form")
    public ResponseEntity<?> submitVehicleInformationForm(
            @RequestBody Map<String, String> formData,
            @RequestParam String formUrl,
            @RequestParam String transactionId,
            @RequestParam String messageId) {

        String formId = formData.get("formId");

        log.info(
                "Vehicle Information form received | txnId={} | formId={} | msgId={}",
                transactionId,
                formId,
                messageId
        );

        return formService.submitVehicleInformationForm(
                formData,
                formUrl,
                transactionId,
                messageId
        );
    }

    @PostMapping("/personal-form")
    public ResponseEntity<?> saveMotorPersonalForm(
            @RequestBody Map<String, String> formData,
            @RequestParam String formUrl,
            @RequestParam String transactionId,
            @RequestParam String messageId) {

        log.info("Received request to save personal form. Transaction ID: {}, Message ID: {}",
                transactionId, messageId);

        return formService.submitPersonalDetailsForm(
                formData,
                formUrl,
                transactionId,
                messageId
        );
    }
}
