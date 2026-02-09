package com.can.buyerApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MotorInsuranceCategoryDTO {
    
    private Long id;
    private String categoryId;
    private String categoryName;
    private String vehicleType;
    private String coverageType;
    private String parentCategoryId;
    
    private String providerId;
    private String providerName;
    
    private String itemId;
    private String itemName;
    private String itemShortDesc;
    
    private String formId;
    private String formUrl;
    private String mimeType;
    private Boolean resubmit;
    private Boolean multipleSubmissions;
    
    private String duration;
    private String durationLabel;
    
    private String transactionId;
    private String messageId;
    
    private Map<String, String> generalInformation;
}