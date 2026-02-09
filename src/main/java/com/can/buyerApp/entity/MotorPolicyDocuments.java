package com.can.buyerApp.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class MotorPolicyDocuments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String transactionId;

    @Column(nullable = false)
    private String messageId;

    // Document details
    private String documentType;   // POLICY_DOC / CLAIM_DOC
    private String code;
    private String name;
    private String shortDesc;
    private String url;
    private String mimeType;

    // Policy & provider
    private String orderId;
    private String orderStatus;   // ACTIVE
    private String providerId;
    private String providerName;

    // Customer contact
    private String email;
    private String phoneNumber;
    private String customerName;
    // Payment
    private String amount;
    private String currency;
    private String paymentStatus;        // PAID
    private String paymentTransactionId;

    // Fulfillment
    private String fulfillmentId;
    private String fulfillmentType;
    private String fulfillmentState;


    @Column(columnDefinition = "TEXT")
    private String policyInfo;

    @Column(nullable = false)
    private Boolean isLatest = true;


    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
