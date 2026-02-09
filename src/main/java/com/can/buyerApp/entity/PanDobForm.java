package com.can.buyerApp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "pan_dob_form")
@Getter
@Setter
public class PanDobForm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String dob;
    private String panValue;
    private String formId;

    private String submissionId;
    private String transactionId;
    private String messageId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
