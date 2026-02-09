package com.can.buyerApp.dto;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FormStatusResponseDTO {

    private String transactionId;
    private String providerId;
    private String providerName;
    private String productName;
    private String formId;
    private String submissionId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
