package com.financial_goal_service.app.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.financial_goal_service.app.client.ProductClient;
import com.financial_goal_service.app.client.dto.ProductResponse;
import com.financial_goal_service.app.client.dto.UsersResponse;
import com.financial_goal_service.app.dto.*;
import com.financial_goal_service.app.entity.FinancialGoal;
import com.financial_goal_service.app.client.AuthClientService;
import com.financial_goal_service.app.constant.GeneralConstant;
import com.financial_goal_service.app.repository.FinancialGoalRepository;
import com.financial_goal_service.app.service.AuditTrailsService;
import com.financial_goal_service.app.service.FinancialGoalService;
import com.financial_goal_service.app.service.InsightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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

    @Autowired
    private ProductClient productClient;

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public List<FinancialGoalResponse> getGoals(String token, String status) throws JsonProcessingException {
        UsersResponse userProfile = authClientService.getUserProfileFromToken(token);
        Long idCust = userProfile.getCustId();

        List<FinancialGoal> goals = financialGoalRepository.findByCustIdAndStatus(idCust, status);

        auditTrailsService.logsAuditTrails(
                GeneralConstant.LOG_ACTIVITY_GET_ALL,
                mapper.writeValueAsString(idCust),
                mapper.writeValueAsString(status),
                "Get All Financial Goal"
        );

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



    public String determineRiskTolerance(Integer age, Double salary) {
        if (age < 30) {
            if (salary > 10000_000) {
                return "Aggressive";
            } else {
                return "Moderate";
            }
        } else if (age <= 45) {
            if (salary > 8_000_000) {
                return "Moderate";
            } else {
                return "Conservative";
            }
        } else {
            return "Conservative";
        }
    }


    @Override
    public FinancialGoalResponse saveFinancialGoal(FinancialGoalRequest financialGoalRequest, String token) throws JsonProcessingException {
        try {
            UsersResponse userProfile = authClientService.getUserProfileFromToken(token);
            Long idCust = userProfile.getCustId();

            String riskTolerance = determineRiskTolerance(userProfile.getAge(), userProfile.getSalary());


            FinancialGoal financialGoal = new FinancialGoal();
            financialGoal.setGoalName(financialGoalRequest.getGoalName());
            financialGoal.setTargetAmount(financialGoalRequest.getTargetAmount());
            financialGoal.setTargetDate(financialGoalRequest.getTargetDate());
            financialGoal.setRiskTolerance(riskTolerance);
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
                GeneralConstant.LOG_ACTIVITY_ARCHIVE,
                mapper.writeValueAsString(goalId),
                mapper.writeValueAsString(responseMessage),
                "Get Financial Goal by Id"
        );


        return responseMessage;
    }
    private Long mapCategoryToId(String categoryName) {
        return switch (categoryName) {
            case "Saham" -> 2L;
            case "Obligasi" -> 3L;
            case "Pasar Uang" -> 4L;
            default -> throw new IllegalArgumentException("Unknown category: " + categoryName);
        };
    }



    @Override
    public SuggestedPortfolioResponse getSuggestedPortfolio(Long goalId) {

        FinancialGoal goal = financialGoalRepository.findByGoalId(goalId)
                .orElseThrow(() -> new RuntimeException("Financial Goal not found"));

        String riskTolerance = goal.getRiskTolerance();

        List<PortfolioAllocation> allocations = switch (riskTolerance) {
            case "Aggressive" -> List.of(
                    PortfolioAllocation.builder().category("Saham").percentage(70).build(),
                    PortfolioAllocation.builder().category("Obligasi").percentage(20).build(),
                    PortfolioAllocation.builder().category("Pasar Uang").percentage(10).build()
            );
            case "Moderate" -> List.of(
                    PortfolioAllocation.builder().category("Saham").percentage(50).build(),
                    PortfolioAllocation.builder().category("Obligasi").percentage(30).build(),
                    PortfolioAllocation.builder().category("Pasar Uang").percentage(20).build()
            );
            case "Conservative" -> List.of(
                    PortfolioAllocation.builder().category("Saham").percentage(30).build(),
                    PortfolioAllocation.builder().category("Obligasi").percentage(50).build(),
                    PortfolioAllocation.builder().category("Pasar Uang").percentage(20).build()
            );
            default -> throw new IllegalArgumentException("Unknown risk tolerance: " + riskTolerance);
        };

        Map<String, List<RecommendedProduct>> recommendedProducts = new HashMap<>();

        for (PortfolioAllocation alloc : allocations) {
            Long categoryId = mapCategoryToId(alloc.getCategory()); // mapping kategori ke ID
            List<ProductResponse> products = productClient.getProductByCategoryId(categoryId);

            List<RecommendedProduct> mapped = products.stream().map(p ->
                    RecommendedProduct.builder()
                            .productName(p.getProductName())
                            .categoryId((p.getCategoryId()))
                            .productRate(p.getProductRate())
                            .productPrice(p.getProductPrice())
                            .build()
            ).toList();

            recommendedProducts.put(alloc.getCategory(), mapped);
        }


        return SuggestedPortfolioResponse.builder()
                .goalId(goalId)
                .suggestedPortfolio(allocations)
                .recommendedProducts(recommendedProducts)
                .build();
    }


    @Override
    public FinancialGoalResponse updateFinancialGoal(Long goalId, String token, FinancialGoalRequest financialGoalRequest) throws JsonProcessingException {
        try {
            UsersResponse userProfile = authClientService.getUserProfileFromToken(token);
            Optional<FinancialGoal> goalOpt = financialGoalRepository.findByGoalId(goalId);
            if (goalOpt.isEmpty()) {
                throw new RuntimeException("Goal not found");
            }

            FinancialGoal goal = goalOpt.get();
            goal.setGoalName(financialGoalRequest.getGoalName());
            goal.setTargetAmount(financialGoalRequest.getTargetAmount());
            goal.setTargetDate(financialGoalRequest.getTargetDate());
            //goal.setRiskTolerance(financialGoalRequest.getRiskTolerance());
            goal.setUpdatedDate(new Date());

            String updatedInsight = insightService.generateInsight(goal);
            goal.setInsightMessage(updatedInsight);

            FinancialGoal updatedGoal = financialGoalRepository.save(goal);

            auditTrailsService.logsAuditTrails(
                    GeneralConstant.LOG_ACTIVITY_UPDATE,
                    mapper.writeValueAsString(financialGoalRequest),
                    mapper.writeValueAsString(updatedGoal),
                    "Update Financial Goal"
            );

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

    @Override
    public FinancialGoalResponse getGoalByName(String goalName, String token) throws JsonProcessingException {
        //Long custId = authClientService.getIdCustFromToken(token);

        Optional<FinancialGoal> goalOpt = financialGoalRepository.findByGoalName(goalName);
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
                    mapper.writeValueAsString(goalName),
                    mapper.writeValueAsString(response),
                    "Get Financial Goal by Name"
            );

            return response;
        } else {
            throw new RuntimeException("Goal not found");
        }
    }
}
