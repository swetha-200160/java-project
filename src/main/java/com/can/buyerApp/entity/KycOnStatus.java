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
public class KycOnStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String transactionId;
    @Column(nullable = false)
    private String messageId;
    private String ekycStatus;
    private String ekycFormId;
    private String ekycSubmissionId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
