package com.can.buyerApp.entity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Entity
public class ClaimDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String policyId;

    @Column(nullable = false)
    private String transactionId;

    @Column(nullable = false)
    private String messageId;
    private String state;
    private String   name;
    private String  short_desc;
    private String url;
    private String type;
    @Lob
    @Column(name = "claim_details", columnDefinition = "TEXT")
    private String claimDetails;
    @Column(name="updated_at")
    private LocalDateTime updatedAt;

    public void setClaimDetails(Map<String, String> claimDetails) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            this.claimDetails = objectMapper.writeValueAsString(claimDetails);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to serialize claimDetails", e);
        }
    }

    public Map<String, String> getClaimDetails() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(this.claimDetails, new TypeReference<>() {});
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to deserialize claimDetails", e);
        }
    }

}
