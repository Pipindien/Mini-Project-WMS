package com.transaction.app.controller;

import com.transaction.app.dto.insight.InsightResponse;
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
    public ResponseEntity<InsightResponse> getInsight(@PathVariable Long goalId, @RequestHeader String token) {
            InsightResponse insightResponse = insightService.generateInsight(goalId, token);
            return ResponseEntity.ok(insightResponse);
    }
}
