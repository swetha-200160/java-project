package com.can.buyerApp.repository;

import com.can.buyerApp.entity.ProposerForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProposerFormRepository extends JpaRepository<ProposerForm,Long> {
}
