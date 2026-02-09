package com.can.buyerApp.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NomineeDetailsDTO {

    private Long id;
    private String transactionId;
    private String messageId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String email;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String phone;
    private String formId;
    private String nomineeForm;
    private String paymentForm;
    private String addOns;
    private String breakupDetails;
    private String totalPrice;
}
