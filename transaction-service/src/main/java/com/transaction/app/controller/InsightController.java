package com.transaction.app.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.transaction.app.dto.insight.InsightResponse;
import com.transaction.app.dto.insight.SimulateInsightRequest;
import com.transaction.app.dto.insight.SimulateProductRequest;
import com.transaction.app.service.InsightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/insights")
public class InsightController {
    @Autowired
    private InsightService insightService;

    @GetMapping("/{goalId}")
    public ResponseEntity<InsightResponse> getInsight(@PathVariable Long goalId, @RequestHeader String token) throws JsonProcessingException {
            InsightResponse insightResponse = insightService.generateInsight(goalId, token);
            return ResponseEntity.ok(insightResponse);
    }

    @PostMapping("/simulate")
    public ResponseEntity<InsightResponse> simulateGoal(@RequestBody SimulateInsightRequest request,
                                                        @RequestHeader String token) throws JsonProcessingException {
        InsightResponse result = insightService.simulateGoalAchievement(request.getGoalId(), request.getMonthlyInvestment(), token);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/simulate-product")
    public ResponseEntity<InsightResponse> simulateProduct(@RequestBody SimulateProductRequest request) throws JsonProcessingException {
        InsightResponse result = insightService.simulateProductInvestment(
                request.getProductId(),
                request.getMonthlyInvestment(),
                request.getYears()
        );
        return ResponseEntity.ok(result);
    }

}
