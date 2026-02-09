package com.can.buyerApp.dto;


import lombok.Data;
import java.util.List;

@Data
public class SearchQuoteResponse {

    private String transactionId;
    private String messageId;
    private String itemId;
    private String parentItemId;
    private String itemName;
    private String price;

    private List<GeneralInfoDTO> generalInfo;
    private List<AddonDTO> addons;
}

