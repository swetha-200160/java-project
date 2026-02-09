package com.can.buyerApp.dto;

import lombok.Data;

@Data
public class PaymentResponseDTO {

    private String transactionId;
    private String status;        // PAID / PENDING / FAILED
    private String type;          // PRE-ORDER
    private String amount;
    private String currency;
    private String collectedBy;

    private String customerName;
    private String customerEmail;
    private String customerPhone;
}
