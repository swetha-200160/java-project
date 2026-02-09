package com.can.buyerApp.repository;

import com.can.buyerApp.entity.MotorPaymentDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MotorPaymentRepository extends JpaRepository<MotorPaymentDetails,Long> {

   Optional<MotorPaymentDetails> findByTransactionIdAndMessageId(String transactionId, String messageId);

   Optional<MotorPaymentDetails> findByTransactionId(String transactionId);

   Optional<MotorPaymentDetails> findTopByTransactionIdOrderByUpdatedAtDesc(String transactionId);
}
