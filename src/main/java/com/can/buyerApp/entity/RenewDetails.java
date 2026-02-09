package com.can.buyerApp.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class RenewDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String policyId;

    @Column(nullable = false)
    private String transactionId;

    @Column(nullable = false)
    private String messageId;
    private String type;
    private String state;
    private String   name;
    private String  short_desc;
    private String url;
    @Column(name="updated_at")
    private LocalDateTime updatedAt;
}