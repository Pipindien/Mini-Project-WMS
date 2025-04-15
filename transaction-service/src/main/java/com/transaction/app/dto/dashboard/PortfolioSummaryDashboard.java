package com.transaction.app.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioSummaryDashboard {
    private double totalInvestment;
    private double estimatedReturn;
    private double totalProfit;
}
