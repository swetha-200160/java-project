package com.can.buyerApp.repository;


import com.can.buyerApp.entity.SearchCatalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SearchCatalogRepository extends JpaRepository<SearchCatalog,Long> {

//    @Query(value = "SELECT * FROM SearchCatalog WHERE transaction_id = :transactionId ORDER BY created_at DESC LIMIT 1", nativeQuery = true)
//    List<SearchCatalog> findByTransactionId(String transactionId);

    @Query("SELECT s FROM SearchCatalog s WHERE s.transactionId = :transactionId AND s.messageId = :messageId")
    List<SearchCatalog> findByTransactionIdAndMessageId(String transactionId, String messageId);

    List<SearchCatalog> findTop2ByTransactionIdOrderByCreatedAtDesc(String transactionId);

    List<SearchCatalog> findByTransactionId(String transactionId);


}
