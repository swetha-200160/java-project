package com.can.buyerApp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class FamilyForm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String submissionId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String relation;
    private String PED;
    private String dob;
    private String panValue;
    private String gender;
    private String diabetes;
    private String bloodPressure;
    private String heartAilments;
    private String other;
    private String weight;
    private String height;
    private String amount;
    private String panIndia;
    private String pincode;
    private String transactionId;
    private String messageId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
