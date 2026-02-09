package com.can.buyerApp.repository;

import com.can.buyerApp.masterentity.PaymentTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentTagRepo extends JpaRepository<PaymentTag,Long> {

    @Query("SELECT p FROM PaymentTag p LEFT JOIN FETCH p.details")
    List<PaymentTag> findAllWithDetails();
}
