package com.can.buyerApp.repository;

import com.can.buyerApp.entity.MotorPolicyDocuments;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MotorDocumentRepository extends JpaRepository<MotorPolicyDocuments,Long> {

//    @Query(value = "SELECT * FROM policy_documents WHERE transaction_id = :transactionId ORDER BY created_at DESC LIMIT 1", nativeQuery = true)
//    List<MotorPolicyDocuments> findByTransactionId(String transactionId);

    List<MotorPolicyDocuments> findByOrderId(String policyId);

    @Query("SELECT p FROM MotorPolicyDocuments p WHERE p.transactionId = :transactionId AND p.messageId = :messageId")
    List<MotorPolicyDocuments> findByTransactionIdAndMessageId(
            @Param("transactionId") String transactionId,
            @Param("messageId") String messageId
    );



    List<MotorPolicyDocuments> findTop3ByTransactionIdOrderByCreatedAtDesc(String transactionId);

    @Query("""
SELECT m.orderId
FROM MotorPolicyDocuments m
WHERE m.transactionId = :transactionId
ORDER BY m.createdAt DESC
""")
    Optional<String> findOrderIdByTransactionId(String transactionId);

//
//    @Transactional
//    @Modifying
//    @Query("""
//UPDATE MotorPolicyDocuments m
//SET m.isLatest = false
//WHERE m.orderId = :orderId
//AND m.documentType = 'POLICY_DOC'
//""")
//    void updateLatestPolicyDoc(String orderId);


    Optional<MotorPolicyDocuments>
    findTopByTransactionIdOrderByCreatedAtDesc(String transactionId);






    List<MotorPolicyDocuments> findByTransactionIdAndIsLatestTrue(String transactionId);


    List<MotorPolicyDocuments> findByOrderIdAndDocumentType(
            String orderId,
            String documentType
    );


}
