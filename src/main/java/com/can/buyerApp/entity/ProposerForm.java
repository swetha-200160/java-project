package com.can.buyerApp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class ProposerForm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String address;
    private String dob;
    private String gender;
    private String email;
    private String phone;
    private String politicallyExposedPerson;
    private String gstin;
    private String height;
    private String question1;
    private String question2;
    private String question3;
    private String question4_1;
    private String question4_2;
    private String submissionId;
    private String transactionId;
    private String messageId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
