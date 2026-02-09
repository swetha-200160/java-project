package com.can.buyerApp.repository;

import com.can.buyerApp.entity.ProposerDetails;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProposerDetailsRepository extends JpaRepository<ProposerDetails,Long> {

   // List<ProposerDetails> findFirstByTransactionId(String transactionId);

    @Query("SELECT p FROM ProposerDetails p WHERE p.transactionId = :transactionId AND p.messageId = :messageId")
    List<ProposerDetails> findByTransactionIdAndMessageId(
            @Param("transactionId") String transactionId,
            @Param("messageId") String messageId
    );


//    @Query(value = "SELECT * FROM proposer_details WHERE transaction_id = :transactionId ORDER BY created_at DESC LIMIT 1", nativeQuery = true)
//    List<ProposerDetails> findByTransactionId(String transactionId);

    List<ProposerDetails> findTop1ByTransactionIdOrderByCreatedAtDesc(String transactionId);
}
