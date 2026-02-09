package com.can.buyerApp.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseDTO {

    private String transactionId;
    private String messageId;
    private String action;
    private String bapId;
    private String bapUri;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String issueId;
}
