package com.can.buyerApp.controller;

import com.can.buyerApp.constants.PreConstants;
import com.can.buyerApp.dto.PolicyDocumentsDTO;
import com.can.buyerApp.request.MotorConfirmRequest;
import com.can.buyerApp.request.MotorOnConfirmRequest;
import com.can.buyerApp.service.ConfirmService;
import com.can.buyerApp.service.OnConfirmService;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.config.InactiveConfigDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;



@Slf4j
@RestController
public class MotorConfirmController {

    private final ConfirmService confirmService;
    private final OnConfirmService onConfirmService;

    public MotorConfirmController(ConfirmService confirmService, OnConfirmService onConfirmService) {
        this.confirmService = confirmService;
        this.onConfirmService = onConfirmService;
    }


    @PostMapping("/confirm")
    public ResponseEntity<?> confirmRequest(@RequestParam String domain,
                                            @RequestParam String type,
                                            @RequestParam String transactionId,
                                            @RequestParam String submissionId,
                                            @RequestParam String formId,
                                            @RequestParam(required = false) String formStatus,
                                            @RequestParam String messageId) {
        try {
            log.info("Received Confirm request with domain: {}, type: {}, transactionId: {}, submissionId: {}",
                    domain, type, transactionId, submissionId);

            if (PreConstants.VALID_DOMAIN.equals(domain) && PreConstants.VALID_TYPES.contains(type) &&
                    StringUtils.isNotBlank(transactionId) && StringUtils.isNotBlank(submissionId)) {

                log.info("Valid domain, type, transactionId, and submissionId. Proceeding to confirm request.");
                return confirmService.sendConfirmRequest(domain, transactionId, submissionId,formId,formStatus,messageId);
            }
            else {
                log.warn("Invalid domain, type, transactionId, or submissionId provided. Domain: {}, Type: {}, transactionId: {}, submissionId: {}",
                        domain, type, transactionId, submissionId);

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid input provided. Expected domain: " + PreConstants.VALID_DOMAIN
                                + ", type: " + PreConstants.VALID_TYPES
                                + ", transactionId: " + transactionId
                                + ", submissionId: " + submissionId);
            }

        } catch (Exception e) {
            log.error("Error occurred while processing the Confirm request", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing the Confirm request. Please try again later.");
        }
    }


    @PostMapping("/on_confirm")
    public ResponseEntity<?> onSelectRequest(@RequestBody MotorOnConfirmRequest onConfirmRequest) {
        try {
            log.info("onConfirmRequest Received successfully with Transaction ID: {}", onConfirmRequest.getContext().getTransaction_id());

            return ResponseEntity.ok(onConfirmService.saveConfirmRequest(onConfirmRequest));

        }
        catch (InactiveConfigDataAccessException e) {
            log.error("Database error occurred while saving onConfirmRequest: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while saving the onConfirmRequest. Please try again later.");
        }
        catch (Exception e) {
            log.error("Unexpected error occurred while processing onConfirmRequest: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while saving on-confirm request.");
        }
    }

    @GetMapping("/policy-documents")
    public List<PolicyDocumentsDTO> getPolicyDocument(@RequestParam String transactionId) {
        return onConfirmService.getPolicyDocuments(transactionId);
    }



}

