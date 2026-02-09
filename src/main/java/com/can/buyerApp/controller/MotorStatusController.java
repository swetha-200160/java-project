package com.can.buyerApp.controller;

import com.can.buyerApp.constants.PreConstants;
import com.can.buyerApp.request.MotorOnStatusRequest;
import com.can.buyerApp.service.OnStatusService;
import com.can.buyerApp.service.StatusService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


@Slf4j
@RestController
public class MotorStatusController {

    private final StatusService statusService;
    private final OnStatusService onStatusService;
    public final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public MotorStatusController(StatusService statusService, OnStatusService onStatusService, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.statusService = statusService;
        this.onStatusService = onStatusService;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/status")
    public ResponseEntity<?> statusRequest(@RequestParam String domain,
                                           @RequestParam String type,
                                           @RequestParam String transactionId) {
        try {
            log.info("Received status request with domain: {}, type: {}, transactionId: {}",
                    domain, type, transactionId);
            if (PreConstants.VALID_DOMAIN.equals(domain) && PreConstants.VALID_TYPES.contains(type) &&
                    StringUtils.isNotBlank(transactionId)) {

                log.info("Valid domain, type, transactionId, and submissionId. Proceeding to send status request.");

                return statusService.sendStatusRequest(domain, transactionId); //call
            }
            else {
                log.warn("Invalid domain or type provided. Domain: {}, Type: {}, transactionId: {}",
                        domain, type, transactionId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid input provided. Expected domain: " + PreConstants.VALID_DOMAIN +
                                ", type: " + PreConstants.VALID_TYPES +
                                ", transactionId: " + transactionId);
            }
        }
        catch (Exception e) {
            log.error("Error occurred while processing the status request", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing the status request. Please try again later.");
        }
    }

    @PostMapping("/on_status")
    public ResponseEntity<?> onStatusCall(@RequestBody MotorOnStatusRequest onStatusRequest) throws JsonProcessingException {
        log.info("Received On Status and Proceeding to Save On Status");
       return ResponseEntity.ok(onStatusService.saveOnStatusRequest(onStatusRequest));

    }


    @GetMapping("/get-form-status")
    public ResponseEntity<?> getFormStatus(
            @RequestParam String transactionId,
            @RequestParam String formId) {

        return onStatusService
                .findByTransactionIdAndFormId(transactionId, formId);
    }


    @GetMapping("/get-payment-status")
    public ResponseEntity<?> getPayment(
            @RequestParam String transactionId) {

        return onStatusService.getPaymentByTransactionId(transactionId);
    }



    @GetMapping("/claim_status/{transactionId}")
    public ResponseEntity<?> getClaimStatus(@PathVariable String transactionId){
        return onStatusService.getClaimStatusByTransactionId(transactionId);

   }

    @GetMapping("/renew_status/{transactionId}")
    public ResponseEntity<?> getRenewStatus(@PathVariable String transactionId){
        return onStatusService.getRenewStatusByTransactionId(transactionId);

    }

    @GetMapping("/cancel_status/{transactionId}")
    public ResponseEntity<?> getCancelStatus(@PathVariable String transactionId){
        return onStatusService.getCancelStatusByTransactionId(transactionId);

    }



}