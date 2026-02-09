package com.can.buyerApp.repository;

import com.can.buyerApp.entity.KycOnStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KycStatusRepository extends JpaRepository<KycOnStatus,Long> {
   KycOnStatus findByTransactionId(String transactionId);
}
