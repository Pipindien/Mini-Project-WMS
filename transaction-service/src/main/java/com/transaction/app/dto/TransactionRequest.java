package com.transaction.app.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
