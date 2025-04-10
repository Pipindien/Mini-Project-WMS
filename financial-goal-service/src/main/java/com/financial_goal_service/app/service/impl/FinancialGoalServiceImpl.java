package com.financial_goal_service.app.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.financial_goal_service.app.entity.FinancialGoal;
import com.financial_goal_service.app.client.AuthClientService;
import com.financial_goal_service.app.constant.GeneralConstant;
import com.financial_goal_service.app.dto.FinancialGoalRequest;
import com.financial_goal_service.app.dto.FinancialGoalResponse;
import com.financial_goal_service.app.dto.SuggestedPortfolioResponse;
import com.financial_goal_service.app.repository.FinancialGoalRepository;
import com.financial_goal_service.app.service.AuditTrailsService;
import com.financial_goal_service.app.service.FinancialGoalService;
import com.financial_goal_service.app.service.InsightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class FinancialGoalServiceImpl implements FinancialGoalService {

    @Autowired
    private FinancialGoalRepository financialGoalRepository;

    @Autowired
    private AuditTrailsService auditTrailsService;

    @Autowired
    private AuthClientService authClientService;

    @Autowired
    private InsightService insightService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public List<FinancialGoalResponse> getGoals(String token, String status) throws JsonProcessingException {
        Long idCust = authClientService.getIdCustFromToken(token);
        List<FinancialGoal> goals = financialGoalRepository.findByCustIdAndStatus(idCust, status);

        return goals.stream().map(goal -> FinancialGoalResponse.builder()
                        .goalId(goal.getGoalId())
                        .goalName(goal.getGoalName())
                        .targetAmount(goal.getTargetAmount())
                        .targetDate(goal.getTargetDate())
                        .riskTolerance(goal.getRiskTolerance())
                        .createdDate(goal.getCreatedDate())
                        .currentAmount(goal.getCurrentAmount())
                        .status(goal.getStatus())
                        .custId(goal.getCustId())
                        .build())
                .toList();

    }

    @Override
    public FinancialGoalResponse saveFinancialGoal(FinancialGoalRequest financialGoalRequest, String token) throws JsonProcessingException {
        try {
            Long idCust = authClientService.getIdCustFromToken(token);

            FinancialGoal financialGoal = new FinancialGoal();
            financialGoal.setGoalName(financialGoalRequest.getGoalName());
            financialGoal.setTargetAmount(financialGoalRequest.getTargetAmount());
            financialGoal.setTargetDate(financialGoalRequest.getTargetDate());
            financialGoal.setRiskTolerance(financialGoalRequest.getRiskTolerance());
            financialGoal.setCreatedDate(new Date());
            financialGoal.setDeleted(false);
            financialGoal.setStatus("Active");
            financialGoal.setCurrentAmount(0);
            financialGoal.setCustId(idCust);

            String insight = insightService.generateInsight(financialGoal);
            financialGoal.setInsightMessage(insight);

            FinancialGoal savedFinancialGoal = financialGoalRepository.save(financialGoal);

            FinancialGoalResponse response = FinancialGoalResponse.builder()
                    .goalId(savedFinancialGoal.getGoalId())
                    .goalName(savedFinancialGoal.getGoalName())
                    .targetAmount(savedFinancialGoal.getTargetAmount())
                    .targetDate(savedFinancialGoal.getTargetDate())
                    .riskTolerance(savedFinancialGoal.getRiskTolerance())
                    .createdDate(savedFinancialGoal.getCreatedDate())
                    .currentAmount(savedFinancialGoal.getCurrentAmount())
                    .status(savedFinancialGoal.getStatus())
                    .custId(savedFinancialGoal.getCustId())
                    .insightMessage(savedFinancialGoal.getInsightMessage())
                    .build();

            auditTrailsService.logsAuditTrails(
                    GeneralConstant.LOG_ACTIVITY_SAVE,
                    mapper.writeValueAsString(financialGoalRequest),
                    mapper.writeValueAsString(response),
                    "Create Financial Goal"
            );

            return response;
        } catch (Exception e) {
            throw new RuntimeException("Failed to save financial goal" + e);
        }
    }

    @Override
    public FinancialGoalResponse getGoalById(Long goalId, String token) throws JsonProcessingException {
        Long custId = authClientService.getIdCustFromToken(token);
        // ✅ Generate insight



        Optional<FinancialGoal> goalOpt = financialGoalRepository.findByGoalId(goalId);
        if (goalOpt.isPresent()) {

            FinancialGoal goal = goalOpt.get();
            String insight = insightService.generateInsight(goal);
            FinancialGoalResponse response = FinancialGoalResponse.builder()
                    .goalId(goal.getGoalId())
                    .goalName(goal.getGoalName())
                    .targetAmount(goal.getTargetAmount())
                    .targetDate(goal.getTargetDate())
                    .riskTolerance(goal.getRiskTolerance())
                    .createdDate(goal.getCreatedDate())
                    .currentAmount(goal.getCurrentAmount())
                    .status(goal.getStatus())
                    .custId(goal.getCustId())
                    .insightMessage(insight)
                    .build();

            auditTrailsService.logsAuditTrails(
                    GeneralConstant.LOG_ACTIVITY_GET_GOALID,
                    mapper.writeValueAsString(goalId),
                    mapper.writeValueAsString(response),
                    "Get Financial Goal by Id"
            );

            return response;
        } else {
            throw new RuntimeException("Goal not found");
        }
    }

    @Override
    public String archiveGoal(Long goalId) throws JsonProcessingException {
        Optional<FinancialGoal> goalOpt = financialGoalRepository.findByGoalId(goalId);
        if (goalOpt.isEmpty()) {
            throw new RuntimeException("Goal not found");
        }

        FinancialGoal goal = goalOpt.get();
        goal.setStatus("Archived");        // update status
        goal.setDeleted(true);             // soft delete flag
        goal.setUpdatedDate(new Date());   // optional: update timestamp

        financialGoalRepository.save(goal);

        String responseMessage = "Goal Archived Successfully";

        auditTrailsService.logsAuditTrails(
                GeneralConstant.LOG_ACTIVITY_GET_GOALID,
                mapper.writeValueAsString(goalId),
                mapper.writeValueAsString(responseMessage),
                "Get Financial Goal by Id"
        );


        return responseMessage;
    }


    @Override
    public SuggestedPortfolioResponse getSuggestedPortfolio(Long goalId) {
        return null;
    }

    @Override
    public FinancialGoalResponse updateFinancialGoal(Long goalId, FinancialGoalRequest financialGoalRequest) throws JsonProcessingException {
        try {
            Optional<FinancialGoal> goalOpt = financialGoalRepository.findByGoalId(goalId);
            if (goalOpt.isEmpty()) {
                throw new RuntimeException("Goal not found");
            }

            FinancialGoal goal = goalOpt.get();
            goal.setGoalName(financialGoalRequest.getGoalName());
            goal.setTargetAmount(financialGoalRequest.getTargetAmount());
            goal.setTargetDate(financialGoalRequest.getTargetDate());
            goal.setRiskTolerance(financialGoalRequest.getRiskTolerance());
            goal.setUpdatedDate(new Date());

            String updatedInsight = insightService.generateInsight(goal);
            goal.setInsightMessage(updatedInsight);

            FinancialGoal updatedGoal = financialGoalRepository.save(goal);

            return FinancialGoalResponse.builder()
                    .goalId(updatedGoal.getGoalId())
                    .goalName(updatedGoal.getGoalName())
                    .targetAmount(updatedGoal.getTargetAmount())
                    .targetDate(updatedGoal.getTargetDate())
                    .riskTolerance(updatedGoal.getRiskTolerance())
                    .createdDate(updatedGoal.getCreatedDate())
                    .currentAmount(updatedGoal.getCurrentAmount())
                    .status(updatedGoal.getStatus())
                    .UpdatedDate(updatedGoal.getUpdatedDate())
                    .custId(updatedGoal.getCustId())
                    .insightMessage(updatedGoal.getInsightMessage())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to update financial goal", e);
        }
    }
}
