package com.can.buyerApp.repository;

import com.can.buyerApp.entity.FamilyForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FamilyFormRepository extends JpaRepository<FamilyForm,Long> {

    @Query(value = "SELECT * FROM family_form WHERE transaction_id = :transactionId order by created_at DESC LIMIT 1", nativeQuery = true)
    FamilyForm findByTransactionId(String transactionId);
}
