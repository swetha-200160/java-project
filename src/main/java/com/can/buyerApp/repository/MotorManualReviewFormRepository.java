package com.can.buyerApp.repository;

import com.can.buyerApp.entity.MotorManualReviewForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MotorManualReviewFormRepository
        extends JpaRepository<MotorManualReviewForm, Long> {
}
