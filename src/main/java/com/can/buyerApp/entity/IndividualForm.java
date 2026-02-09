package com.can.buyerApp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class IndividualForm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ped;
    private String coverageAmount;
    private String bloodPressure;
    private String diabetes;
    private String dob;
    private String email;
    private String firstName;
    private String gender;
    private String heartAilments;
    private String height;
    private String lastName;
    private String other;
    private String panIndia;
    private String panValue;
    private String phone;
    private String pinCode;
    private String weight;
    private String submissionId;
    private String transactionId;
    private String messageId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
