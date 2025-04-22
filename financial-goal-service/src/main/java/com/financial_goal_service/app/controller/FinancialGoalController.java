package com.financial_goal_service.app.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.financial_goal_service.app.dto.FinancialGoalRequest;
import com.financial_goal_service.app.dto.FinancialGoalResponse;
import com.financial_goal_service.app.dto.SuggestedPortfolioResponse;
import com.financial_goal_service.app.dto.UpdateProgressRequest;
import com.financial_goal_service.app.service.FinancialGoalService;
import com.financial_goal_service.app.service.impl.FinancialGoalServiceImpl;
import com.financial_goal_service.app.utility.RestApiPath;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping(RestApiPath.FINANCIALGOAL_API_PATH)
public class FinancialGoalController {
    @Autowired
    FinancialGoalService financialGoalService;

    @PostMapping(RestApiPath.FINANCIALGOAL_SAVE)
    public ResponseEntity<FinancialGoalResponse> saveFinancialGoal(@Valid @RequestBody FinancialGoalRequest financialGoalRequest, @RequestHeader String token) throws JsonProcessingException {
        FinancialGoalResponse response = financialGoalService.saveFinancialGoal(financialGoalRequest, token);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping(RestApiPath.FINANCIALGOAL_UPDATE)
    public ResponseEntity<FinancialGoalResponse> updateFinancialGoal (@PathVariable Long goalId, @RequestHeader String token, @Valid @RequestBody  FinancialGoalRequest financialGoalRequest) throws JsonProcessingException {
        FinancialGoalResponse response = financialGoalService.updateFinancialGoal(goalId, token, financialGoalRequest);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/")
    public ResponseEntity<List<FinancialGoalResponse>> getGoals(
            @RequestHeader String token,
            @RequestParam(required = false) String status
    ) throws JsonProcessingException {
        List<FinancialGoalResponse> goals = financialGoalService.getGoals(token, status);
        return ResponseEntity.ok(goals);
    }

    @GetMapping("/{goalId}")
    public ResponseEntity<FinancialGoalResponse> getGoalById(
            @PathVariable Long goalId,
            @RequestHeader String token
    ) throws JsonProcessingException {
        FinancialGoalResponse goal = financialGoalService.getGoalById(goalId, token);
        return ResponseEntity.ok(goal);
    }

    @GetMapping("/all/{goalId}")
    public ResponseEntity<FinancialGoalResponse> getAllGoals(
            @PathVariable Long goalId,
            @RequestHeader String token
    ) throws JsonProcessingException {
        FinancialGoalResponse goal = financialGoalService.getAllGoals(goalId, token);
        return ResponseEntity.ok(goal);
    }

    @PatchMapping(RestApiPath.FINANCIALGOAL_ARCHIVE)
    public ResponseEntity<String> archiveGoal(@PathVariable Long goalId) throws JsonProcessingException {
        String message = financialGoalService.archiveGoal(goalId);
        return ResponseEntity.ok(message);
    }


    @GetMapping("/{goalId}/suggested-portfolio")
    public ResponseEntity<SuggestedPortfolioResponse> getSuggestedPortfolio(@PathVariable Long goalId) throws JsonProcessingException {
        SuggestedPortfolioResponse response = financialGoalService.getSuggestedPortfolio(goalId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get/{goalName}")
    public ResponseEntity<FinancialGoalResponse> getGoalByName(
            @PathVariable String goalName,
            @RequestHeader String token
    ) throws JsonProcessingException {
        FinancialGoalResponse goal = financialGoalService.getGoalByName(goalName, token);
        return ResponseEntity.ok(goal);
    }

    @PutMapping("/{id}/update-progress")
    public ResponseEntity<String> updateProgress(
            @PathVariable Long id,
            @RequestBody UpdateProgressRequest request,
            @RequestHeader("token") String token
    ) throws JsonProcessingException {
        financialGoalService.updateProgress(id, request);
        return ResponseEntity.ok("Progress updated successfully");
    }

}
