package com.can.buyerApp.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProposerDetailsDTO {

    private Long id;
    private String transactionId;
    private String messageId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String email;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String phone;
    private String formId;
    private String formUrl;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String fulfillmentId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String fulfillmentType;
}
