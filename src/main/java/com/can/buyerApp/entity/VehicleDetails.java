package com.can.buyerApp.entity;


import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;


@Data
@Entity
@Table(name = "vehicle_details")
public class VehicleDetails {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "transaction_id")
    private String transactionId;
    
    @Column(name = "message_id")
    private String messageId;
    
    @Column(name = "registration_number")
    private String registrationNumber;
    
    @Column(name = "vehicle_make")
    private String vehicleMake;
    
    @Column(name = "vehicle_model")
    private String vehicleModel;
    
    @Column(name = "vehicle_variant")
    private String vehicleVariant;
    
    @Column(name = "fuel_type")
    private String fuelType;
    
    @Column(name = "registration_date")
    private String registrationDate;
    
    @Column(name = "manufacturing_year")
    private String manufacturingYear;
    
    @Column(name = "owner_name")
    private String ownerName;
    
    @Column(name = "owner_email")
    private String ownerEmail;
    
    @Column(name = "owner_phone")
    private String ownerPhone;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
