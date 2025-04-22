package com.transaction.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.transaction.app.dto.portosum.PortfolioSummaryResponse;

public interface PortfolioSummaryService {
    PortfolioSummaryResponse upsertPortfolioSummary(Long goalId,  String token) throws JsonProcessingException;


    void updateProgress(Long goalId, String token) throws JsonProcessingException;

    PortfolioSummaryResponse getPortfolioOverview(String token) throws JsonProcessingException;

    PortfolioSummaryResponse getPortfolioDetail(String token, Long goalId) throws JsonProcessingException;
}
