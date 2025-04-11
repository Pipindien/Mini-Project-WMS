package com.financial_goal_service.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.financial_goal_service.app.dto.FinancialGoalRequest;
import com.financial_goal_service.app.dto.FinancialGoalResponse;
import com.financial_goal_service.app.dto.SuggestedPortfolioResponse;

import java.util.List;

public interface FinancialGoalService {

    List<FinancialGoalResponse> getGoals(String token, String status) throws JsonProcessingException;

    FinancialGoalResponse saveFinancialGoal(FinancialGoalRequest request, String token) throws JsonProcessingException;

    FinancialGoalResponse getGoalById(Long goalId, String token) throws JsonProcessingException;

    String archiveGoal(Long goalId) throws JsonProcessingException;

    SuggestedPortfolioResponse getSuggestedPortfolio(Long goalId);

    FinancialGoalResponse updateFinancialGoal(Long goalId, String token, FinancialGoalRequest financialGoalRequest) throws JsonProcessingException;

    FinancialGoalResponse getGoalByName(String goalName, String token) throws JsonProcessingException;

}

