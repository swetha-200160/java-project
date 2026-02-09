package com.can.buyerApp.repository;

import com.can.buyerApp.entity.Progress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProgressRepository extends JpaRepository<Progress,Long> {


    Optional<Progress> findByUserId(Long id);

    @Query(value = "SELECT * FROM progress WHERE user_id = :userId ORDER BY updated_at DESC LIMIT 1", nativeQuery = true)
    Optional<Progress> findLatestByUserId(@Param("userId") Long userId);


    Optional<Progress> findByTransactionId(String transactionId);
}
