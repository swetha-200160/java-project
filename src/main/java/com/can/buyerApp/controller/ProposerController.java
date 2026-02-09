package com.can.buyerApp.controller;

import com.can.buyerApp.service.ProposerDetailsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;


@RestController
public class ProposerController {

    private final ProposerDetailsService proposerDetailsService;

    public ProposerController(ProposerDetailsService proposerDetailsService) {
        this.proposerDetailsService = proposerDetailsService;
    }


    @GetMapping("/init-proposer")
    public ResponseEntity<?> getProposerTransactionId(@RequestParam String transactionId, @RequestParam String messageId) {
        return proposerDetailsService.getProposerTransactionId(transactionId,messageId);
    }

    @GetMapping("/proposer-url")
    public Map<String,String> getForm(@RequestParam String transactionId, @RequestParam String formUrl) {
        return proposerDetailsService.getForm(transactionId,formUrl);
    }

}
