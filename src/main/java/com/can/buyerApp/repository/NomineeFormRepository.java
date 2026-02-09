package com.can.buyerApp.repository;

import com.can.buyerApp.entity.NomineeForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NomineeFormRepository extends JpaRepository<NomineeForm,Long> {
}
