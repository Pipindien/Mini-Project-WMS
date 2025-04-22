package com.transaction.app.dto.insight;


import lombok.Data;

@Data
public class SimulateInsightRequest {
    private Long goalId;
    private double monthlyInvestment;
}

