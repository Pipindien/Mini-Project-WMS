package com.transaction.app.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.transaction.app.dto.portosum.PortfolioSummaryResponse;
import com.transaction.app.service.PortfolioSummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/portfolio")
public class PortfolioSummaryController {
    @Autowired
    PortfolioSummaryService portfolioSummaryService;

    @PutMapping("/update/{goalId}")
    public ResponseEntity<PortfolioSummaryResponse> updatePortfolioSummary(
            @PathVariable Long goalId,
            @RequestHeader String token) throws JsonProcessingException {

        PortfolioSummaryResponse response = portfolioSummaryService.upsertPortfolioSummary(goalId, token);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<PortfolioSummaryResponse> getPortfolioDashboard(@RequestHeader String token) throws JsonProcessingException {
        PortfolioSummaryResponse response = portfolioSummaryService.getPortfolioOverview(token);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/dashboard/{goalId}")
    public ResponseEntity<PortfolioSummaryResponse> getPortfolioDashboardByGoalId(@RequestHeader String token, @PathVariable Long goalId) throws JsonProcessingException {
        portfolioSummaryService.upsertPortfolioSummary(goalId, token);
        portfolioSummaryService.updateProgress(goalId, token);
        PortfolioSummaryResponse response = portfolioSummaryService.getPortfolioDetail(token, goalId);
        return ResponseEntity.ok(response);
    }

}
