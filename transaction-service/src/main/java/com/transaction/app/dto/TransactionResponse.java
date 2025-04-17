package com.transaction.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponse {
    private String trxNumber;
    private String status;
    private Double amount;
    private Long custId;
    private Long productId;
    private String productName;
    private String goalName;
    private Long goalId;
    private Double productPrice;
    private Integer lot;
    private String notes;
}
