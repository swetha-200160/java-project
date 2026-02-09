package com.can.buyerApp.repository;

import com.can.buyerApp.entity.MotorVehicleForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MotorVehicleFormRepository extends JpaRepository<MotorVehicleForm, Long> {

    @Query(value = "SELECT * FROM motor_vehicle_form WHERE transaction_id = :transactionId order by created_at DESC LIMIT 1", nativeQuery = true)
    MotorVehicleForm findByTransactionId(String transactionId);
}
