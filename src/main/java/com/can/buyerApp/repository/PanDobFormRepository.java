package com.can.buyerApp.repository;

import com.can.buyerApp.entity.PanDobForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PanDobFormRepository extends JpaRepository<PanDobForm, Long> {
}
