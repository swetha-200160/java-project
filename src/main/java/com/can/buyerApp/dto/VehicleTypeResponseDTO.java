package com.can.buyerApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleTypeResponseDTO {
    
    private String transactionId;
    private List<VehicleType> availableVehicleTypes;
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class VehicleType {
        private String type;  // TWO_WHEELER or FOUR_WHEELER
        private String displayName;  // "Two Wheeler" or "Four Wheeler"
        private int providerCount;  // Number of providers offering this type
        private int categoryCount;  // Number of categories for this type
        
        public VehicleType(String type, String displayName) {
            this.type = type;
            this.displayName = displayName;
        }
    }
}