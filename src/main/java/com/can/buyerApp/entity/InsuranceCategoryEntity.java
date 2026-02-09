package com.can.buyerApp.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Entity
@Table(name="insurance_category")
public class InsuranceCategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String category_name;

    @Column(nullable = false)
    private String category_id;

    // NEW FIELDS for Motor Insurance
    @Column(name = "vehicle_type", length = 50)
    private String vehicleType;  // TWO_WHEELER or FOUR_WHEELER

    @Column(name = "coverage_type", length = 100)
    private String coverageType;  // COMPREHENSIVE, THIRD_PARTY, OWN_DAMAGE

    @Column(name = "parent_category_id", length = 50)
    private String parentCategoryId;  // C10, C11, etc.

    // Form Details
    private String form_id;
    private String mime_type;
    private String form_url;
    private String resubmit;
    private String multiple_submissions;

    // Transaction Details
    @Column(nullable = false)
    private String message_id;
    @Column(nullable = false)
    private String transactionId;

    // Provider Details
    private String providerName;
    private String providerId;

    // Item Details
    private String itemId;
    private String itemName;
    @Column(name = "item_short_desc", columnDefinition = "TEXT")
    private String itemShortDesc;

    // Duration
    @Column(name = "duration")
    private String duration;  // P1Y for 1 year

    @Column(name = "duration_label")
    private String durationLabel;  // TENURE

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // General Information stored as JSON
    @Column(name = "general_information", columnDefinition = "TEXT")
    private String generalInformation;

    public Map<String, Object> getGeneralInformation() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(generalInformation, Map.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}