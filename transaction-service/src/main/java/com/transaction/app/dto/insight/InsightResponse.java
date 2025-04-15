package com.transaction.app.dto.insight;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InsightResponse {
    private Long goalId;
    private String insightMessage;
    private Double futureValue;
    private Double monthlyInvestmentNeeded;
}
