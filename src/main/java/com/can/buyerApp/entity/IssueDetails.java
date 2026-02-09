package com.can.buyerApp.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class IssueDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String policyId;

    @Column(nullable = false)
    private String transactionId;
    private String messageId;
    private String issueId;
    private String status;
    private String shortDescription;
    private  String customerName;
    private String CustomerEmail;
    private String CustomerPhoneNumber;
    private String OrganizationName;
    private String resolutionProviderName;
    private String resolutionProviderEmail;
    private String resolutionProviderPhoneNo;
    private LocalDateTime updatedAt;

}
