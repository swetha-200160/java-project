package com.can.buyerApp.repository;

import com.can.buyerApp.entity.IndividualForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IndividualFormRepository extends JpaRepository<IndividualForm,Long> {

    @Query(value = "SELECT * FROM individual_form WHERE transaction_id = :transactionId order by created_at DESC LIMIT 1", nativeQuery = true)
    IndividualForm findByTransactionId(String transactionId);
}
