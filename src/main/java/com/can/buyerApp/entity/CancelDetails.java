package com.can.buyerApp.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
public class CancelDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_id")
    private String itemId;

    private String policyId;

    @Column(name = "transaction_id",nullable = false)
    private String transaction_id;

    @Column(nullable = false)
    private String messageId;

    @Column(name = "quote_id")
    private String quoteId;

    @Column(name = "status")
    private String status;

    @Column(name="updated_at")
    private LocalDateTime updatedAt;

}
