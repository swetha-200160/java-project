package com.can.buyerApp.controller;

import com.can.buyerApp.constants.PreConstants;
import com.can.buyerApp.masterentity.CancelReason;
import com.can.buyerApp.request.OnCancelRequest;
import com.can.buyerApp.service.CancelService;
import com.can.buyerApp.service.OnCancelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
public class CancelController {

    private final CancelService cancelService;
    private  final OnCancelService onCancelService;

    public CancelController(CancelService cancelService, OnCancelService onCancelService) {
        this.cancelService = cancelService;
        this.onCancelService = onCancelService;
    }


    @PostMapping("/cancel")
    public ResponseEntity<?> cancelRequest(@RequestParam String domain, @RequestParam String type, @RequestParam String transactionId,
                                           @RequestParam Long cancellationReasonId, @RequestParam String orderId, @RequestParam String description) {
        try {
            log.info("Received cancel request with TransactionId: {}", transactionId);

            if (PreConstants.VALID_DOMAIN.equals(domain) && PreConstants.VALID_TYPES.contains(type)&&
                    Objects.nonNull(transactionId)) {
                log.info("Valid domain and type. Proceeding to send cancel request");
                return cancelService.sendCancelRequest(domain,type,transactionId,cancellationReasonId,orderId,description);
            }
            else {
                log.warn("Invalid domain or type provided. Domain: {}, Type: {}", domain, type);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid domain or type provided. Expected domain: " + PreConstants.VALID_DOMAIN +
                                ", types: " + PreConstants.VALID_TYPES);
            }
        }
        catch (Exception e) {
            log.error("Error occurred while processing the cancel request", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing the cancel request. Please try again later.");
        }
    }


    @PostMapping("/on_cancel")
    public ResponseEntity<?> onCancelRequest(@RequestBody OnCancelRequest onCancelRequest) {

        if (Objects.isNull(onCancelRequest)) {
            log.warn("Received invalid OnCancelRequest: {}", onCancelRequest);
            return ResponseEntity.badRequest().body("Invalid onCancelRequest. Must not be null.");
        }

        try {
            log.info("Proceeding to save on cancel request");
            return ResponseEntity.ok(onCancelService.saveOnCancelRequest(onCancelRequest));
        }
        catch (Exception e) {
            log.error("An error occurred while processing onCancelRequest: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing on-Cancel Request.");
        }
    }

    @GetMapping("/cancel_reason")
    public List<CancelReason> getReason(){
        return onCancelService.getCancelReason();
    }
}
