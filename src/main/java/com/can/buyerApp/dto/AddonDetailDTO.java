package com.can.buyerApp.dto;

import lombok.Data;

@Data
public class AddonDetailDTO {

    private String addonId;
    private String addonName;
    private String addonPrice;

    public AddonDetailDTO(){

    }

    public AddonDetailDTO(String addonId, String addonName, String addonPrice) {
        this.addonId = addonId;
        this.addonName = addonName;
        this.addonPrice = addonPrice;
    }
}
