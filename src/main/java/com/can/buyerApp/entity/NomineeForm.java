package com.can.buyerApp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.sql.Date;
import java.time.LocalDateTime;

@Entity
@Data
public class NomineeForm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String dob;
    private String relation;
    private String submissionId;
    private String transactionId;
    private String messageId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
