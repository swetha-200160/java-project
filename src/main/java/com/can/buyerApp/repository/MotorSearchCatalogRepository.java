//// MotorSearchCatalogRepository.java
//package com.can.buyerApp.repository;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//import java.util.List;
//
//@Repository
//public interface MotorSearchCatalogRepository extends JpaRepository<MotorSearchCatalog, Long> {
//
//    @Query("SELECT m FROM MotorSearchCatalog m WHERE m.transactionId = :transactionId AND m.messageId = :messageId")
//    List<SearchCatalog> findByTransactionIdAndMessageId(@Param("transactionId") String transactionId,
//                                                              @Param("messageId") String messageId);
//}