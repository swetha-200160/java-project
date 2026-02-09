package com.can.buyerApp.repository;

import com.can.buyerApp.entity.AddonDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddOnsRepository extends JpaRepository<AddonDetail,Long> {
    List<AddonDetail> findBySearchCatalogId(Long searchCatalogId);

}
