//package com.can.buyerApp.repository;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//
//import com.can.buyerApp.entity.MotorNomineeForm;
//
//public interface MotorNomineeFormRepository extends JpaRepository<MotorNomineeForm, Long> {
//    
//    @Query(value = "SELECT * FROM motor_nominee_form WHERE transaction_id = :transactionId ORDER BY created_at DESC LIMIT 1", nativeQuery = true)
//    MotorNomineeForm findByTransactionId(String transactionId);
//
//
//}
