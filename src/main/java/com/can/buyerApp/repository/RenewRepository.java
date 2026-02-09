package com.can.buyerApp.repository;

import com.can.buyerApp.entity.RenewDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RenewRepository extends JpaRepository<RenewDetails,Long> {

    @Query(value = "select * from renew_details where transaction_id = :transactionId order by updated_at DESC LIMIT 1", nativeQuery = true)
    Optional<RenewDetails> findByTransactionId(@Param("transactionId") String transactionId);
}
