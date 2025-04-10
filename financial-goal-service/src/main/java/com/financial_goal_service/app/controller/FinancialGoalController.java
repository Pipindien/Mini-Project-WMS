package com.financial_goal_service.app.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.financial_goal_service.app.dto.FinancialGoalRequest;
import com.financial_goal_service.app.dto.FinancialGoalResponse;
import com.financial_goal_service.app.service.FinancialGoalService;
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
    public ResponseEntity<FinancialGoalResponse> updateFinancialGoal (@PathVariable Long goalId, @Valid @RequestBody  FinancialGoalRequest financialGoalRequest) throws JsonProcessingException {
        FinancialGoalResponse response = financialGoalService.updateFinancialGoal(goalId, financialGoalRequest);
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

    @PatchMapping(RestApiPath.FINANCIALGOAL_ARCHIVE)
    public ResponseEntity<String> archiveGoal(@PathVariable Long goalId) throws JsonProcessingException {
        String message = financialGoalService.archiveGoal(goalId);
        return ResponseEntity.ok(message);
    }









}
