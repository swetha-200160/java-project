package com.can.buyerApp.repository;

import com.can.buyerApp.entity.OnSelectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OnSelectRepository extends JpaRepository<OnSelectEntity,Long> {

    @Query(value = "SELECT * FROM onselect_details WHERE transaction_id = :transactionId order by created_at DESC LIMIT 1", nativeQuery = true)
    OnSelectEntity findByTransactionId(String transactionId);



    @Query("SELECT o FROM OnSelectEntity o WHERE o.transaction_id = :transactionId AND o.messageId = :messageId")
    OnSelectEntity findByTransactionIdAndMessageId(
            @Param("transactionId") String transactionId,
            @Param("messageId") String messageId
    );
}
