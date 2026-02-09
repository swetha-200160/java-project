package com.can.buyerApp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MotorPaymentDetails {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type")
    private String type;

    @Column(name = "status")
    private String status;

    @Column(name = "collected_by")
    private String collectedBy;

    @Column(name = "amount")
    private String amount;

    @Column(name = "bank_account_number")
    private String bankAccountNumber;

    @Column(name = "bank_code")
    private String bankCode;

    @Column(name = "currency")
    private String currency;

    @Column(name = "transaction_id", nullable = false)
    private String transactionId;

    @Column(name = "message_id", nullable = false)
    private String messageId;

    private String customerName;
    private String customerEmail;
    private String customerPhone;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
