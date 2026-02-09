package com.can.buyerApp.repository;

import com.can.buyerApp.entity.InsuranceCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InsuranceCategoryRepository extends JpaRepository<InsuranceCategoryEntity, Long> {

    // Existing method
    @Query(value = "SELECT * FROM insurance_category WHERE transaction_id = :transactionId AND provider_id = :providerId AND message_id = :messageId", nativeQuery = true)
    List<InsuranceCategoryEntity> findByTransactionIdAndProviderIdAndMessageId(
            @Param("transactionId") String transactionId,
            @Param("providerId") String providerId,
            @Param("messageId") String messageId);

    @Query(value = "SELECT * FROM insurance_category " +
            "WHERE transaction_id = :transactionId " +
            "AND provider_id = :providerId " +
            "AND message_id = :messageId " +
            "AND category_id = :categoryId",
            nativeQuery = true)
    List<InsuranceCategoryEntity> findByTransactionIdAndProviderIdAndMessageIdAndCategoryId(
            @Param("transactionId") String transactionId,
            @Param("providerId") String providerId,
            @Param("messageId") String messageId,
            @Param("categoryId") String categoryId);

    // Find by transaction ID only
    @Query(value = "SELECT * FROM insurance_category WHERE transaction_id = :transactionId", nativeQuery = true)
    List<InsuranceCategoryEntity> findByTransactionId(@Param("transactionId") String transactionId);

    // NEW METHODS for Motor Insurance

    /**
     * Find by transaction ID and vehicle type
     */
    @Query(value = "SELECT * FROM insurance_category WHERE transaction_id = :transactionId AND vehicle_type = :vehicleType", nativeQuery = true)
    List<InsuranceCategoryEntity> findByTransactionIdAndVehicleType(
            @Param("transactionId") String transactionId,
            @Param("vehicleType") String vehicleType);

    /**
     * Find by transaction ID, vehicle type, and provider ID
     */
    @Query(value = "SELECT * FROM insurance_category WHERE transaction_id = :transactionId AND vehicle_type = :vehicleType AND provider_id = :providerId", nativeQuery = true)
    List<InsuranceCategoryEntity> findByTransactionIdAndVehicleTypeAndProviderId(
            @Param("transactionId") String transactionId,
            @Param("vehicleType") String vehicleType,
            @Param("providerId") String providerId);

    /**
     * Find by transaction ID, vehicle type, and coverage type
     */
    @Query(value = "SELECT * FROM insurance_category WHERE transaction_id = :transactionId AND vehicle_type = :vehicleType AND coverage_type = :coverageType", nativeQuery = true)
    List<InsuranceCategoryEntity> findByTransactionIdAndVehicleTypeAndCoverageType(
            @Param("transactionId") String transactionId,
            @Param("vehicleType") String vehicleType,
            @Param("coverageType") String coverageType);

    /**
     * Find by transaction ID, message ID, and vehicle type
     */
    @Query(value = "SELECT * FROM insurance_category WHERE transaction_id = :transactionId AND message_id = :messageId AND vehicle_type = :vehicleType", nativeQuery = true)
    List<InsuranceCategoryEntity> findByTransactionIdAndMessageIdAndVehicleType(
            @Param("transactionId") String transactionId,
            @Param("messageId") String messageId,
            @Param("vehicleType") String vehicleType);

    /**
     * Find by transaction ID, provider ID, message ID, and vehicle type
     */
    @Query(value = "SELECT * FROM insurance_category WHERE transaction_id = :transactionId AND provider_id = :providerId AND message_id = :messageId AND vehicle_type = :vehicleType", nativeQuery = true)
    List<InsuranceCategoryEntity> findByTransactionIdAndProviderIdAndMessageIdAndVehicleType(
            @Param("transactionId") String transactionId,
            @Param("providerId") String providerId,
            @Param("messageId") String messageId,
            @Param("vehicleType") String vehicleType);

    /**
     * Find all unique vehicle types for a transaction
     */
    @Query(value = "SELECT DISTINCT vehicle_type FROM insurance_category WHERE transaction_id = :transactionId AND vehicle_type IS NOT NULL", nativeQuery = true)
    List<String> findDistinctVehicleTypeByTransactionId(@Param("transactionId") String transactionId);
}