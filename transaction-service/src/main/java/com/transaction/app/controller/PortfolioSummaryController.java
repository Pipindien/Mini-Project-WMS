package com.transaction.app.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.transaction.app.dto.portosum.PortfolioSummaryRequest;
import com.transaction.app.dto.portosum.PortfolioSummaryResponse;
import com.transaction.app.service.PortfolioSummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/portfolio")
public class PortfolioSummaryController {
    @Autowired
    PortfolioSummaryService portfolioSummaryService;

    @PutMapping("/update/{goalId}")
    public ResponseEntity<PortfolioSummaryResponse> updatePortfolioSummary(
            @PathVariable Long goalId, // ✅ fix di sini: hapus underscore
            @RequestHeader String token) throws JsonProcessingException {

        PortfolioSummaryResponse response = portfolioSummaryService.upsertPortfolioSummary(goalId, token);
        return ResponseEntity.ok(response);
    }


}
