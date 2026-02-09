package com.can.buyerApp.repository;

import com.can.buyerApp.masterentity.CancelReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CancelReasonRepo extends JpaRepository<CancelReason,Long> {
    CancelReason findAllById(Long cancellationReasonId);
}
