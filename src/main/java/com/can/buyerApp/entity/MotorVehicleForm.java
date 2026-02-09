package com.can.buyerApp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "motor_vehicle_form")
@Getter
@Setter
public class MotorVehicleForm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Personal Information
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String gender;

    // Vehicle Basic Information
    private String vehicleType;
    private String registrationNumber;
    private String vehicleUniqueCode;
    private String rtoCode;

    @Column(name = "registration_date")
    private LocalDate registrationDate;

    private String make;
    private String model;
    private String variant;
    private String fuelType;
    private String manufactureYear;
    private String engineNumber;
    private String chassisNumber;
    private String cubicCapacity;
    private String seatingCapacity;

    // Policy Information
    private String policyType;
    private String coverType;
    private String idv;
    private String personalAccidentCover;
    private String paTenure;
    private String policyTenure;
    private String ncb;

    // Previous Policy Information
    private String previousPolicyNumber;
    private String previousPolicyInsurerName;
    private String previousPolicyType;

    @Column(name = "previous_policy_expiry_date")
    private LocalDate previousPolicyDate;

    private String previousPolicyCustomerName;
    private String previousInsurer;
    private String claimHistory;
    private Boolean claimStatus;

    // Ownership
    private String ownerType;

    // Submission metadata
    private String submissionId;
    private String transactionId;
    private String messageId;
    private String formId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}