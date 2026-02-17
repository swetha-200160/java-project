package com.can.buyerApp.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class AppConfig {

@Value("${payment.default.collected_by:BAP}")
private String collectedBy;


    @Value("${payment.default.status}")
    private String paymentStatus;

    @Value("${payment.default.type}")
    private String paymentType;

    @Value("${payment.params.bank_account_number}")
    private String bankAccountNumber;

    @Value("${payment.params.bank_code}")
    private String bankCode;

    @Value("${payment.params.currency}")
    private String currency;


}
