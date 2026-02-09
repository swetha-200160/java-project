package com.can.buyerApp.repository;

import com.can.buyerApp.entity.FormStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FormStatusRepository extends JpaRepository<FormStatus, Long> {

    Optional<FormStatus> findByTransactionIdAndSubmissionId(
            String transactionId,
            String submissionId
    );

    Optional<FormStatus> findByTransactionIdAndFormId(String transactionId, String formId);
}
