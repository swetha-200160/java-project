package com.can.buyerApp.dto;

import lombok.Data;

@Data
public class ProviderDTO {
    private Long id;
    private String providerName;
    private String providerId;
    private String transactionId;
    private String providerUrl;
}
