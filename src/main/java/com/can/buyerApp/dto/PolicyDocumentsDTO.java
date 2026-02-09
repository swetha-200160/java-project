package com.can.buyerApp.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PolicyDocumentsDTO {


    // Identifiers
    private Long id;
    private String transactionId;
    private String messageId;

    // Document
    private String documentType;   // POLICY_DOC / CLAIM_DOC
    private String code;
    private String name;
    private String shortDesc;
    private String url;
    private String mimeType;

    // Policy
    private String policyId;
    private String policyStatus;

    // Provider
    private String providerId;
    private String providerName;

    // Customer
    private String customerName;
    private String email;
    private String phoneNumber;

    // Payment
    private String amount;
    private String currency;
    private String paymentStatus;
    private String paymentTransactionId;

    // Fulfillment
    private String fulfillmentId;
    private String fulfillmentType;
    private String fulfillmentState;

    private String policyInfo;


}
