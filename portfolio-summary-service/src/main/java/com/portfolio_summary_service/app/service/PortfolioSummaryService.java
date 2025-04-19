package com.portfolio_summary_service.app.service;

import com.portfolio_summary_service.app.dto.PortfolioSummaryResponse;
import com.portfolio_summary_service.app.dto.fingoaldto.UpdateCurrentAmountResponse;

import java.util.List;
import java.util.Map;

public interface PortfolioSummaryService {
    PortfolioSummaryResponse getSummaryByGoalId(Long goalId, Long custId, String token);


    List<Map<String, Object>> getAllSummariesByCustomerId(Long custId);
    UpdateCurrentAmountResponse recalculate(Long goalId);
}
