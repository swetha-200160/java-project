package com.can.buyerApp.repository;

import com.can.buyerApp.entity.GeneralInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GeneralInformationRepo extends JpaRepository<GeneralInformation,Long> {

    List<GeneralInformation> findBySearchCatalogId(Long searchCatalogId);
}
