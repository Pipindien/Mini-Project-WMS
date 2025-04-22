package com.transaction.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.transaction.app.dto.insight.InsightResponse;

public interface InsightService {
    InsightResponse generateInsight(Long goalId, String token) throws JsonProcessingException;

    InsightResponse simulateGoalAchievement(Long goalId, double monthlyInvestment, String token) throws JsonProcessingException;

    InsightResponse simulateProductInvestment(Long productId, double monthlyInvestment, int years) throws JsonProcessingException;

}
