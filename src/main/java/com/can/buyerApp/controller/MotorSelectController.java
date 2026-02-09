package com.can.buyerApp.controller;

import com.can.buyerApp.request.MotorOnselectRequest;
import com.can.buyerApp.service.MotorOnSelectService;
import com.can.buyerApp.service.MotorSelectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.config.InactiveConfigDataAccessException;
import org.springframework.web.bind.annotation.RestController;
import com.can.buyerApp.constants.PreConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Objects;


@Slf4j
@RestController
public class MotorSelectController {

    private final MotorSelectService motorSelectService;
    private final MotorOnSelectService motorOnSelectService;

    public MotorSelectController(MotorSelectService motorSelectService, MotorOnSelectService motorOnSelectService) {
        this.motorSelectService = motorSelectService;
        this.motorOnSelectService = motorOnSelectService;
    }

    @PostMapping("/select")
    public ResponseEntity<?> selectRequest(@RequestParam String domain,
                                           @RequestParam String type,
                                           @RequestParam String transactionId,
                                           @RequestParam(required = false) List<String> addons,
                                           @RequestParam String itemId,
                                           @RequestParam(required = false) String formStatus,
                                           @RequestParam(required = false) String formId,
                                           @RequestParam(required = false) String submissionId) {
        try {
            log.info("Received motor insurance select request with domain: {}, type: {}, transactionId: {}, addons: {}, itemId: {}, vehicleRegNo: {}, formId: {}, submissionId: {}",
                    domain, type, transactionId, addons, itemId, formStatus, formId, submissionId);

            if (PreConstants.VALID_DOMAIN.equals(domain) &&
                    PreConstants.VALID_TYPES.contains(type) &&
                    StringUtils.isNotBlank(transactionId) &&
                    StringUtils.isNotBlank(itemId)) {

                log.info("Valid input. Proceeding to send motor insurance select request.");
                return motorSelectService.sendSelectRequest(domain, transactionId, addons, itemId, formStatus, formId, submissionId);

            } else {
                log.warn("Invalid input provided.");
                String errorMessage = String.format(
                        "Invalid input provided. Expected domain: %s, valid types: %s, non-blank transactionId and itemId.",
                        PreConstants.VALID_DOMAIN, PreConstants.VALID_TYPES
                );
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
            }
        } catch (Exception e) {
            log.error("Error occurred while processing the motor insurance select request", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing the motor insurance select request. Please try again later.");
        }
    }

    @PostMapping("/on_select")
    public ResponseEntity<?> onSelectRequest(@RequestBody MotorOnselectRequest motorOnselectRequest) {

        if (Objects.isNull(motorOnselectRequest)) {
            log.warn("Received invalid MotorOnselectRequest: {}", motorOnselectRequest);
            return ResponseEntity.badRequest().body("Invalid MotorOnselectRequest. Must not be null.");
        }

        try {
            log.info("Received Motor Insurance On Select Request with Transaction ID: {}", 
                    motorOnselectRequest.getContext().getTransaction_id());
            return ResponseEntity.ok(motorOnSelectService.saveSelectRequest(motorOnselectRequest));
        } catch (InactiveConfigDataAccessException e) {
            log.error("Database error occurred while saving MotorOnselectRequest: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while saving the MotorOnselectRequest. Please try again later.");
        } catch (Exception e) {
            log.error("An error occurred while saving Motor Insurance On select Request: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while saving Motor Insurance On select Request");
        }
    }


    @GetMapping("/select-quotes")
    public ResponseEntity<?> getQuoteByTransactionId(@RequestParam String transactionId, 
                                                      @RequestParam String messageId) {
        return motorOnSelectService.getQuoteByTransactionId(transactionId, messageId);
    }


}