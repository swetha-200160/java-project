package com.can.buyerApp.repository;


import com.can.buyerApp.entity.VehicleInformationForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehicleInformationFormRepository
        extends JpaRepository<VehicleInformationForm, Long> {

}
