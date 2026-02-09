package com.can.buyerApp.repository;

import com.can.buyerApp.entity.CancelDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CancelRepository extends JpaRepository<CancelDetails,Long> {

    @Query(value = "SELECT * FROM cancel_details WHERE transaction_id = :transactionId order by updated_at DESC LIMIT 1", nativeQuery = true)
    Optional<CancelDetails> findByTransactionId(String transactionId);
}
