package com.can.buyerApp.controller;

import com.can.buyerApp.service.NomineeDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;


@Slf4j
@RestController
public class NomineeDetailsController {

    private final NomineeDetailsService nomineeDetailsService;

    public NomineeDetailsController(NomineeDetailsService nomineeDetailsService) {
        this.nomineeDetailsService = nomineeDetailsService;
    }

    @GetMapping("/init-nominee")
    public ResponseEntity<?> nomineeTransactionId(@RequestParam String transactionId,@RequestParam String messageId) {
        log.info("Fetching nominee form for the respective transactionId: {}",transactionId);
        return nomineeDetailsService.getNomineeTransactionId(transactionId,messageId);
    }

    @GetMapping("/forms")
    public Map<String,String> getForm(@RequestParam String transactionId, @RequestParam String formUrl) {
        return nomineeDetailsService.getForm(transactionId,formUrl);
    }
}
