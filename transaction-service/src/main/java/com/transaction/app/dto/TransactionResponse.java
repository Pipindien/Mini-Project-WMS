package com.transaction.app.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionResponse {
    private String status;
    private Double amount;
    private Long custId;
    private Long productId;
    private Long goalId;
    private Double productPrice;
    private Integer lot;
    private String notes;
}
