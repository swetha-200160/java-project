package com.can.buyerApp.controller;

import com.can.buyerApp.constants.PreConstants;
import com.can.buyerApp.request.OnUpdateRequest;
import com.can.buyerApp.service.MotorOnUpdateService;
import com.can.buyerApp.service.MotorUpdateService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;



@Slf4j
@RestController
public class MotorUpdateController {

    private final MotorOnUpdateService motorOnUpdateService;
    private final MotorUpdateService motorUpdateService;
    public MotorUpdateController(MotorOnUpdateService onUpdateService, MotorUpdateService motorUpdateService) {
        this.motorOnUpdateService = onUpdateService;
        this.motorUpdateService = motorUpdateService;
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateRequest(@RequestParam String domain, @RequestParam String transactionId, @RequestParam String phoneNumber) {

        try {
            log.info("Received Update request | domain={} | txnId={}",
                    domain, transactionId);

            if (!PreConstants.VALID_DOMAIN.equals(domain) || StringUtils.isBlank(transactionId)
                    || StringUtils.isBlank(phoneNumber)) {

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid input parameters");
            }
            return motorUpdateService.sendUpdateRequest(domain, transactionId, phoneNumber);

        } catch (Exception e) {
            log.error("Error occurred while processing Update request", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing the Update request");
        }
    }




    @PostMapping("on_update")
    public ResponseEntity<?> saveOnUpdateRequest(@RequestBody OnUpdateRequest onUpdateRequest) throws JsonProcessingException {
        try{
            log.info("Received On Update Response and Proceeding to save");
            return  ResponseEntity.ok(motorOnUpdateService.saveOnUpdate(onUpdateRequest));
        }
        catch (JsonProcessingException e) {
            log.error("Error processing JSON request: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid JSON format: " + e.getMessage());
        }
        catch (Exception e) {
            log.error("An error occurred while processing on-update request: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }
}
