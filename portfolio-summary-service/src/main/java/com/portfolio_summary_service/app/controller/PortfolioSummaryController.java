package com.portfolio_summary_service.app.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.portfolio_summary_service.app.dto.PortfolioSummaryResponse;
import com.portfolio_summary_service.app.service.PortfolioSummaryService;
import com.portfolio_summary_service.app.service.impl.PortfolioSummaryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("portfolio-summary")
public class PortfolioSummaryController {

    @Autowired
    private PortfolioSummaryServiceImpl portfolioSummaryServiceImpl;

    @GetMapping("/getSummaryByGoalId/{goalId}")
    public ResponseEntity<PortfolioSummaryResponse> getSummaryByGoalId(
            @PathVariable Long goalId,
            @RequestHeader("token") String token,
            @RequestParam(required = false) Long custId
    ) {
        PortfolioSummaryResponse summary = portfolioSummaryServiceImpl.getSummaryByGoalId(goalId, custId, token);
        return ResponseEntity.ok(summary);
    }

}
