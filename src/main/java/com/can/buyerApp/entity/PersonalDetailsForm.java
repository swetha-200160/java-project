package com.can.buyerApp.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "personal_details_form")
public class PersonalDetailsForm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ===== CONTEXT =====
    @Column(nullable = false)
    private String transactionId;

    @Column(nullable = false)
    private String messageId;

    @Column(nullable = false)
    private String formId;

    @Column(nullable = false)
    private String submissionId;

    // ===== PERSONAL DETAILS =====
    private String name;

    @Column(columnDefinition = "TEXT")
    private String address;

    private String dob;

    private String gender;

    private String email;

    private String phone;

    // ===== AUDIT =====
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
