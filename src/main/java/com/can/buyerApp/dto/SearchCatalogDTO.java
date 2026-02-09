package com.can.buyerApp.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SearchCatalogDTO {

    private Long id;
    private String transactionId;
    private String messageId;
    private String itemId;
    private String itemName;
    private String price;
    private Map<String, String> generalInformation;
    private List<AddonDetailDTO> addonDetails;

    public  SearchCatalogDTO(){

    }
    public SearchCatalogDTO(Long id, String transactionId,String messageId, String itemId, String itemName, String price, Map<String, String> generalInformation, List<AddonDetailDTO> addonDetails) {
        this.id = id;
        this.transactionId = transactionId;
        this.messageId=messageId;
        this.itemId = itemId;
        this.itemName = itemName;
        this.price = price;
        this.generalInformation = generalInformation;
        this.addonDetails = addonDetails;
    }
}
