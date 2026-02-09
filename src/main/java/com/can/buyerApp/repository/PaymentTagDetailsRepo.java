package com.can.buyerApp.repository;

import com.can.buyerApp.masterentity.PaymentTagDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentTagDetailsRepo extends JpaRepository<PaymentTagDetail,Long> {

}
