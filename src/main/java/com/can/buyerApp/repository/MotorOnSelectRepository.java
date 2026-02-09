package com.can.buyerApp.repository;


import com.can.buyerApp.entity.MotorOnSelectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MotorOnSelectRepository extends JpaRepository<MotorOnSelectEntity, Long> {

    @Query("SELECT m FROM MotorOnSelectEntity m WHERE m.transaction_id = :transactionId AND m.messageId = :messageId")
    MotorOnSelectEntity findByTransactionIdAndMessageId(@Param("transactionId") String transactionId, 
                                                        @Param("messageId") String messageId);
}


