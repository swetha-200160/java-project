package com.can.buyerApp.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name="context_details")
public class ContextEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bap_id;
    private String bap_uri;
    private String bpp_id;
    private String bpp_uri;
    private String domain;
    private String location_country_code;
    @Column(name = "message_id", nullable = false)
    private String message_id;
    private String timestamp;
    @Column(name = "transaction_id", nullable = false)
    private String transaction_id;
    private String ttl;
    private String version;
    private String providerId;
    private String providerName;
    @Column(name = "isSelected", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isSelected;
    private String providerUrl;
    private String formId;

    // NEW FIELD for Motor Insurance
    @Column(name = "item_id")
    private String itemId;  // Store the item ID associated with this context

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}