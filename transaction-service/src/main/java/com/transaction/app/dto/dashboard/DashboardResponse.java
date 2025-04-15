package com.transaction.app.dto.dashboard;

import com.transaction.app.client.dto.FinancialGoalResponse;
import com.transaction.app.dto.insight.InsightResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    private PortfolioSummaryDashboard portfolioSummaryDashboard;
    private List<FinancialGoalResponse> activeGoals;
}

