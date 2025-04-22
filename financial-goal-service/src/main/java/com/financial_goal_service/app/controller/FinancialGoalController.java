package com.financial_goal_service.app.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.financial_goal_service.app.advice.dto.RestApiResponse;
import com.financial_goal_service.app.dto.*;
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

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping(RestApiPath.FINANCIALGOAL_API_PATH)
public class FinancialGoalController {
    @Autowired
    FinancialGoalService financialGoalService;

    @PostMapping(RestApiPath.FINANCIALGOAL_SAVE)
    public ResponseEntity<RestApiResponse<FinancialGoalResponse>> saveFinancialGoal(@Valid @RequestBody FinancialGoalRequest financialGoalRequest, @RequestHeader String token) throws JsonProcessingException {
        FinancialGoalResponse response = financialGoalService.saveFinancialGoal(financialGoalRequest, token);
        RestApiResponse<FinancialGoalResponse> apiResponse = RestApiResponse.<FinancialGoalResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Financial goal created successfully")
                .timestamp(LocalDateTime.now())
                .data(response)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping(RestApiPath.FINANCIALGOAL_UPDATE)
    public ResponseEntity<RestApiResponse<FinancialGoalResponse>> updateFinancialGoal (@PathVariable Long goalId, @RequestHeader String token, @Valid @RequestBody  FinancialGoalRequest financialGoalRequest) throws JsonProcessingException {
        FinancialGoalResponse response = financialGoalService.updateFinancialGoal(goalId, token, financialGoalRequest);
        RestApiResponse<FinancialGoalResponse> apiResponse = RestApiResponse.<FinancialGoalResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Financial goal has been successfully updated.")
                .timestamp(LocalDateTime.now())
                .data(response)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/")
    public ResponseEntity<RestApiResponse<List<FinancialGoalResponse>>> getGoals(
            @RequestHeader String token,
            @RequestParam(required = false) String status
    ) throws JsonProcessingException {
        List<FinancialGoalResponse> goals = financialGoalService.getGoals(token, status);
        RestApiResponse<List<FinancialGoalResponse>> response = RestApiResponse.<List<FinancialGoalResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("List of financial goals fetched successfully")
                .timestamp(LocalDateTime.now())
                .data(goals)
                .build();

        return ResponseEntity.ok(response);
    }


    @GetMapping("/{goalId}")
    public ResponseEntity<RestApiResponse<FinancialGoalResponse>> getGoalById(
            @PathVariable Long goalId,
            @RequestHeader String token
    ) throws JsonProcessingException {
        FinancialGoalResponse goal = financialGoalService.getGoalById(goalId, token);
        RestApiResponse<FinancialGoalResponse> apiResponse = RestApiResponse.<FinancialGoalResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Successfully Get financial goal by Id")
                .timestamp(LocalDateTime.now())
                .data(goal)
                .build();
        return ResponseEntity.ok(apiResponse);
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
    public ResponseEntity<RestApiResponse<String>> archiveGoal(@PathVariable Long goalId) throws JsonProcessingException {
        String message = financialGoalService.archiveGoal(goalId);

        RestApiResponse<String> response = RestApiResponse.<String>builder()
                .status(HttpStatus.OK.value())
                .message(message)
                .timestamp(LocalDateTime.now())
                .data(null) // atau bisa kasih message juga di sini kalau mau
                .build();
        return ResponseEntity.ok(response);
    }


    @GetMapping("/{goalId}/suggested-portfolio")
    public ResponseEntity<RestApiResponse<SuggestedPortfolioResponse>> getSuggestedPortfolio(@PathVariable Long goalId) throws JsonProcessingException {
        SuggestedPortfolioResponse response = financialGoalService.getSuggestedPortfolio(goalId);

        RestApiResponse<SuggestedPortfolioResponse> apiResponse = RestApiResponse.<SuggestedPortfolioResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Suggested portfolio retrieved successfully")
                .timestamp(LocalDateTime.now())
                .data(response)
                .build();

        return ResponseEntity.ok(apiResponse);
    }


    @GetMapping("/get/{goalName}")
    public ResponseEntity<FinancialGoalResponse> getGoalByName(
            @PathVariable String goalName,
            @RequestHeader String token
    ) throws JsonProcessingException {
        FinancialGoalResponse goal = financialGoalService.getGoalByName(goalName, token);
        return ResponseEntity.ok(goal);
    }

    @PutMapping("/{goalId}/update-progress")
    public ResponseEntity<RestApiResponse<UpdateProgressResponse>> updateProgress(
            @PathVariable Long goalId,
            @RequestBody UpdateProgressRequest request,
            @RequestHeader("token") String token
    ) throws JsonProcessingException {
        financialGoalService.updateProgress(goalId, request);
        UpdateProgressResponse response = financialGoalService.updateProgress(goalId, request);

        RestApiResponse<UpdateProgressResponse> apiResponse = new RestApiResponse<>();
        apiResponse.setStatus(HttpStatus.OK.value());
        apiResponse.setMessage("Goal progress updated successfully");
        apiResponse.setTimestamp(LocalDateTime.parse(LocalDateTime.now().toString()));
        apiResponse.setData(response);

        return ResponseEntity.ok(apiResponse);
    }

}
