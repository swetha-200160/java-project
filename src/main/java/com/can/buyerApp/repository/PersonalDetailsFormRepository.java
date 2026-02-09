package com.can.buyerApp.repository;

import com.can.buyerApp.entity.PersonalDetailsForm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonalDetailsFormRepository
        extends JpaRepository<PersonalDetailsForm, Long> {
}
