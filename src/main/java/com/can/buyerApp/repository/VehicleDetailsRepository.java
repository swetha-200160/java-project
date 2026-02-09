package com.can.buyerApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.can.buyerApp.entity.VehicleDetails;

public interface VehicleDetailsRepository extends JpaRepository<VehicleDetails, Long> {
    VehicleDetails findByTransactionId(String transactionId);
    VehicleDetails findByTransactionIdAndMessageId(String transactionId, String messageId);

}
