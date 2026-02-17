package com.can.buyerApp.repository;

import com.can.buyerApp.entity.ContextEntity;
import com.can.buyerApp.entity.InsuranceCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ContextRepository extends JpaRepository<Context, Long> {
}



    @Query(value = "SELECT * FROM context_details WHERE transaction_id = :transactionId" +
            " AND provider_id = :providerId " +
            "AND message_id= :messageId",
            nativeQuery = true)
    ContextEntity findByTransactionAndProviderIdAndMessageId(@Param("transactionId") String transactionId,
                                                             @Param("providerId") String providerId,
                                                             @Param("messageId") String messageId);

//     newly added for form url fetching purpose
    @Query(value = "SELECT * FROM context_details WHERE transaction_id = :transactionId" +
            " AND provider_id = :providerId ",
            nativeQuery = true)
    ContextEntity findByTransactionAndProviderId(@Param("transactionId") String transactionId,
                                                             @Param("providerId") String providerId);

    @Query(value = "SELECT * FROM context_details WHERE transaction_id = :transactionId " +
            "AND provider_id = :providerId " +
            "AND message_id = :messageId " +
            "AND form_id = :formId",
            nativeQuery = true)
    ContextEntity findByTransactionAndProviderIdAndMessageIdAndFormId(
            @Param("transactionId") String transactionId,
            @Param("providerId") String providerId,
            @Param("messageId") String messageId,
            @Param("formId") String formId
    );




    @Query(value = """
    SELECT cd.*
    FROM context_details cd
    JOIN insurance_category ic
        ON cd.transaction_id = ic.transaction_id
       AND cd.provider_id = ic.provider_id
       AND cd.message_id = ic.message_id
    WHERE cd.form_id = :formId
      AND ic.category_id = :categoryId
      AND cd.transaction_id = :transactionId
      AND cd.provider_id = :providerId
      AND cd.message_id = :messageId
    """,
            nativeQuery = true)
    ContextEntity findContextByCategoryId(
            @Param("transactionId") String transactionId,
            @Param("providerId") String providerId,
            @Param("messageId") String messageId,
            @Param("formId") String formId,
            @Param("categoryId") String categoryId
    );



    @Query(value = "SELECT * FROM context_details WHERE transaction_id = :transactionId AND is_selected = true", nativeQuery = true)
    ContextEntity findByTransactionAndIsSelected(@Param("transactionId") String transactionId);

    @Query(value = "SELECT * FROM context_details WHERE transaction_id = :transactionId AND message_id = :messageId", nativeQuery = true)
    List<ContextEntity> findByTransactionIdAndMessageId(
            @Param("transactionId") String transactionId,
            @Param("messageId") String messageId);

    @Query(value = "SELECT * FROM context_details WHERE transaction_id = :transactionId", nativeQuery = true)
    List<ContextEntity> findByTransactionId(String transactionId);

}