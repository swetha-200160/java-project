package com.can.buyerApp.entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "proposer_detail")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProposerDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transaction_id", nullable = false)
    private String transactionId;

    @Column(name = "message_id", nullable = false)
    private String messageId;

    @Column(name = "email", nullable = true)
    private String email;

    @Column(name = "phone", nullable = true)
    private String phone;

    @Column(name = "form_id", nullable = true)
    private String formId;

    @Column(name = "form_url", nullable = true)
    private String formUrl;

    private String fulfillmentId;

    private String fulfillmentType;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
