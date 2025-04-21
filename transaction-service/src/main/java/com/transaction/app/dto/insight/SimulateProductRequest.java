package com.transaction.app.dto.insight;

import lombok.Data;

@Data
public class SimulateProductRequest {
    private Long productId;
    private double monthlyInvestment;
    private int years;
}

