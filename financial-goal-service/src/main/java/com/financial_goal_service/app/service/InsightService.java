package com.financial_goal_service.app.service;

import com.financial_goal_service.app.entity.FinancialGoal;

public interface InsightService {
    String generateInsight(FinancialGoal goal);
}
