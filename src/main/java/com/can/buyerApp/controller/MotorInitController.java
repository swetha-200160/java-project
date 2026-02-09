package com.can.buyerApp.controller;

import com.can.buyerApp.constants.PreConstants;
import com.can.buyerApp.request.OnInitRequest;
import com.can.buyerApp.service.MotorInitService;
import com.can.buyerApp.service.MotorOnInitService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.config.InactiveConfigDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;


@Slf4j
@RestController
public class MotorInitController {

    private final MotorInitService motorInitService;
    private final MotorOnInitService firstOnInitService;

    public MotorInitController(MotorInitService motorInitService, MotorOnInitService firstMotorOnInitService) {
        this.motorInitService = motorInitService;
        this.firstOnInitService = firstMotorOnInitService;
    }

    @PostMapping("/init")
    public ResponseEntity<?> initRequest(@RequestParam String domain,
                                         @RequestParam String type,
                                         @RequestParam String transactionId,
                                         @RequestParam(required = false) String submissionId,
                                         @RequestParam(required = false) String formStatus,
                                         @RequestParam String messageId) {
        try {
            log.info("Received Init request with domain: {} and type: {}", domain, type);
            if (PreConstants.VALID_DOMAIN.equals(domain) && PreConstants.VALID_TYPES.contains(type) &&
                    StringUtils.isNotBlank(transactionId)) {
                return motorInitService.sendInitRequest(domain, transactionId, submissionId, formStatus, messageId);
            } else {
                log.warn("Invalid domain or type provided. Domain: {}, Type: {}", domain, type);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid domain or type provided. Expected domain: " + PreConstants.VALID_DOMAIN + ", type: " + PreConstants.VALID_TYPES);
            }
        } catch (Exception e) {
            log.error("Error occurred while processing the Init request", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing the Init request. Please try again later.");
        }
    }


    @PostMapping("/on_init")
    public ResponseEntity<?> onInitRequest(@RequestBody OnInitRequest onInitRequest) {

        if (Objects.isNull(onInitRequest)) {
            log.warn("Received invalid OnInitRequest: {}", onInitRequest);
            return ResponseEntity.badRequest().body("Invalid OnInitRequest. Must not be null.");
        }
        try {
            log.info("OnInitRequest Received successfully with Transaction ID: {}", onInitRequest.getContext().getTransaction_id());
            return ResponseEntity.ok(firstOnInitService.saveOnInitRequest(onInitRequest));
        } catch (InactiveConfigDataAccessException e) {
            log.error("Database error occurred while saving OnInitRequest: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while saving the OnInitRequest. Please try again later.");
        } catch (Exception e) {
            log.error("An error occurred while processing OnInitRequest: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An  error occurred while saving on-init request");
        }
    }
}
