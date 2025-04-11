package com.transaction.app.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionRequest {
    private String status;
    private Double amount;
    private String productName;
    private String goalName;
    private String notes;
}
