package com.transaction.app.service;

import com.transaction.app.dto.insight.InsightResponse;

public interface InsightService {
    InsightResponse generateInsight(Long goalId, String token);

    InsightResponse simulateGoalAchievement(Long goalId, double monthlyInvestment, String token);

    InsightResponse simulateProductInvestment(Long productId, double monthlyInvestment, int years);

}
