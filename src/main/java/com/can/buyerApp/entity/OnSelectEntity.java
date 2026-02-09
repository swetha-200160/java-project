package com.can.buyerApp.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@Entity
@Table(name = "onselect_details")
public class OnSelectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_id")
    private String itemId;

    @Column(name = "transaction_id",nullable = false)
    private String transaction_id;

    @Column(name = "message_id",nullable = false)
    private String messageId;

    @Column(name = "parent_item_id")
    private String parentItemId;

    @Column(name = "item_name")
    private String itemName;

    @Column(name = "general_information", columnDefinition = "TEXT")
    private String generalInformation;

    @Column(name = "time_duration")
    private String timeDuration;

    @Column(name = "form_id")
    private String formId;

    @Column(name = "form_url")
    private String formUrl;

    @Column(name = "add_ons", columnDefinition = "TEXT")
    private String addOns;

    @Column(name = "quote_id")
    private String quoteId;

    @Column(name = "breakup_details", columnDefinition = "TEXT")
    private String breakupDetails;

    @Column(name = "total_price")
    private String totalPrice;

    @Column(name = "ttl")
    private String ttl;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Helper methods to deserialize the JSON strings into proper Java structures
    public Map<String, Object> getGeneralInformation() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(generalInformation, Map.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Map<String, Object>> getAddOns() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(addOns, List.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Map<String, Object> getBreakupDetails() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(breakupDetails, Map.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
