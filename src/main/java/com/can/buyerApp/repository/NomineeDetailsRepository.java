package com.can.buyerApp.repository;

import com.can.buyerApp.entity.NomineeDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NomineeDetailsRepository  extends JpaRepository<NomineeDetails, Long> {

    List<NomineeDetails> findFirstByTransactionId(String transactionId);

    @Query("SELECT n FROM NomineeDetails n WHERE n.transactionId = :transactionId AND n.messageId = :messageId")
    List<NomineeDetails> findByTransactionIdAndMessageId(
            @Param("transactionId") String transactionId,
            @Param("messageId") String messageId
    );

    List<NomineeDetails> findTop1ByTransactionIdOrderByCreatedAtDesc(String transactionId);

//    @Query(value = "SELECT * FROM nominee_details WHERE transaction_id = :transactionId ORDER BY created_at DESC LIMIT 1", nativeQuery = true)
//    List<NomineeDetails> findByTransactionId(String transactionId);
}