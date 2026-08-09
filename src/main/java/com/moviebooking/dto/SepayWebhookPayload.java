package com.moviebooking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SepayWebhookPayload {
    private Long id;
    private String gateway;
    private String transactionDate;
    private String accountNumber;
    private String subAccount;
    private BigDecimal amountIn;
    private BigDecimal amountOut;
    private BigDecimal accumulated;
    private String code;
    private String transactionContent;
    private String referenceNumber;
    private String body;
}
