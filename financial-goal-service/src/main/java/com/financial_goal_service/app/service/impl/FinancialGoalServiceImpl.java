package com.financial_goal_service.app.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.financial_goal_service.app.advice.exception.GoalNotFoundException;
import com.financial_goal_service.app.client.ProductClient;
import com.financial_goal_service.app.client.dto.CategoryResponse;
import com.financial_goal_service.app.client.dto.ProductResponse;
import com.financial_goal_service.app.client.dto.UsersResponse;
import com.financial_goal_service.app.dto.*;
import com.financial_goal_service.app.entity.FinancialGoal;
import com.financial_goal_service.app.client.AuthClientService;
import com.financial_goal_service.app.constant.GeneralConstant;
import com.financial_goal_service.app.repository.FinancialGoalRepository;
import com.financial_goal_service.app.service.AuditTrailsService;
import com.financial_goal_service.app.service.FinancialGoalService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class FinancialGoalServiceImpl implements FinancialGoalService {

    @Autowired
    private FinancialGoalRepository financialGoalRepository;

    @Autowired
    private AuditTrailsService auditTrailsService;

    @Autowired
    private AuthClientService authClientService;

    @Autowired
    private ProductClient productClient;

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public List<FinancialGoalResponse> getGoals(String token, String status) throws JsonProcessingException {
        try {
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
                            .insightMessage(goal.getInsightMessage())
                            .build())
                    .toList();
        }
        catch (Exception e) {
            throw new GoalNotFoundException("Goal Not Found");
        }
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
            financialGoal.setCurrentAmount(0.0);
            financialGoal.setInsightMessage("Kamu sudah punya tujuan yang jelas! Yuk mulai investasi pertamamu untuk mencapainya.");
            financialGoal.setCustId(idCust);

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
                    .insightMessage(goal.getInsightMessage())
                    .build();

            auditTrailsService.logsAuditTrails(
                    GeneralConstant.LOG_ACTIVITY_GET_GOALID,
                    mapper.writeValueAsString(goalId),
                    mapper.writeValueAsString(response),
                    "Get Financial Goal by Id"
            );

            return response;
        } else {
            throw new GoalNotFoundException("Goal not found");
        }
    }

    @Override
    public FinancialGoalResponse getAllGoals(Long goalId, String token) throws JsonProcessingException {

        Optional<FinancialGoal> goalOpt = financialGoalRepository.findAllByGoalId(goalId);
        if (goalOpt.isPresent()) {

            FinancialGoal goal = goalOpt.get();
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
                    .insightMessage(goal.getInsightMessage())
                    .build();

            auditTrailsService.logsAuditTrails(
                    GeneralConstant.LOG_ACTIVITY_GET_GOALID,
                    mapper.writeValueAsString(goalId),
                    mapper.writeValueAsString(response),
                    "Get Financial Goal by Id Without Deleted"
            );

            return response;
        } else {
            throw new GoalNotFoundException("Goal not found");
        }
    }

    @Override
    public String archiveGoal(Long goalId) throws JsonProcessingException {
        Optional<FinancialGoal> goalOpt = financialGoalRepository.findByGoalId(goalId);
        if (goalOpt.isEmpty()) {
            throw new GoalNotFoundException("Goal not found");
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


    @Override
    public FinancialGoalResponse updateFinancialGoal(Long goalId, String token, FinancialGoalRequest financialGoalRequest) throws JsonProcessingException {
        try {
            UsersResponse userProfile = authClientService.getUserProfileFromToken(token);
            Optional<FinancialGoal> goalOpt = financialGoalRepository.findByGoalId(goalId);
            if (goalOpt.isEmpty()) {
                throw new GoalNotFoundException("Goal not found");
            }

            FinancialGoal goal = goalOpt.get();
            goal.setGoalName(financialGoalRequest.getGoalName());
            goal.setTargetAmount(financialGoalRequest.getTargetAmount());
            goal.setTargetDate(financialGoalRequest.getTargetDate());
            goal.setRiskTolerance(financialGoalRequest.getRiskTolerance());
            goal.setUpdatedDate(new Date());

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

        Optional<FinancialGoal> goalOpt = financialGoalRepository.findByGoalName(goalName);
        if (goalOpt.isPresent()) {

            FinancialGoal goal = goalOpt.get();
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
                    .insightMessage(goal.getInsightMessage())
                    .build();

            auditTrailsService.logsAuditTrails(
                    GeneralConstant.LOG_ACTIVITY_GET_GOALID,
                    mapper.writeValueAsString(goalName),
                    mapper.writeValueAsString(response),
                    "Get Financial Goal by Name"
            );

            return response;
        } else {
            throw new GoalNotFoundException("Goal not found");
        }
    }


    @Override
    public SuggestedPortfolioResponse getSuggestedPortfolio(Long goalId) throws JsonProcessingException {
        FinancialGoal goal = financialGoalRepository.findByGoalId(goalId)
                .orElseThrow(() -> new GoalNotFoundException("Financial Goal not found"));

        List<CategoryResponse> categories = productClient.getAllCategories();
        if (categories == null || categories.isEmpty()) {
            throw new IllegalStateException("Category list is empty or null");
        }

        String riskTolerance = goal.getRiskTolerance();
        List<PortfolioAllocation> allocations = getPortfolioAllocationByRiskTolerance(riskTolerance, categories);

        Map<String, List<RecommendedProduct>> recommendedProducts = new HashMap<>();

        for (PortfolioAllocation alloc : allocations) {
            List<RecommendedProduct> products = getRecommendedProductsByCategoryId(alloc.getCategoryId());
            recommendedProducts.put(alloc.getCategoryType(), products);
        }

        // Log audit trail
        auditTrailsService.logsAuditTrails(
                GeneralConstant.LOG_ACTIVITY_GET_SUGGESTED_PORTO,
                mapper.writeValueAsString(goalId),
                mapper.writeValueAsString(goalId),
                "Get Suggested Portfolio"
        );

        // Return the final response
        return SuggestedPortfolioResponse.builder()
                .goalId(goalId)
                .suggestedPortfolio(allocations)
                .recommendedProducts(recommendedProducts)
                .build();
    }

    private List<PortfolioAllocation> getPortfolioAllocationByRiskTolerance(String riskTolerance, List<CategoryResponse> categories) {
        return switch (riskTolerance) {
            case "Aggressive" -> getPortfolioAllocations(categories, this::getPercentageForAggressive);
            case "Moderate" -> getPortfolioAllocations(categories, this::getPercentageForModerate);
            case "Conservative" -> getPortfolioAllocations(categories, this::getPercentageForConservative);
            default -> throw new IllegalArgumentException("Unknown risk tolerance: " + riskTolerance);
        };
    }

    private List<PortfolioAllocation> getPortfolioAllocations(List<CategoryResponse> categories,
                                                              Function<String, Integer> getPercentageFunction) {
        return categories.stream()
                .map(category -> {
                    String categoryType = category.getCategoryType();
                    Integer percentage = getPercentageFunction.apply(categoryType);
                    if (percentage == null) {
                        throw new IllegalArgumentException("Unknown category: " + categoryType);
                    }
                    return new PortfolioAllocation(
                            categoryType,
                            percentage,
                            category.getCategoryId()
                    );
                })
                .collect(Collectors.toList());
    }

    // Allocation percentage rules
    private Integer getPercentageForAggressive(String categoryType) {
        return switch (categoryType) {
            case "Saham" -> 70;
            case "Obligasi" -> 20;
            case "Pasar Uang" -> 10;
            default -> null;
        };
    }

    private Integer getPercentageForModerate(String categoryType) {
        return switch (categoryType) {
            case "Saham" -> 50;
            case "Obligasi" -> 30;
            case "Pasar Uang" -> 20;
            default -> null;
        };
    }

    private Integer getPercentageForConservative(String categoryType) {
        return switch (categoryType) {
            case "Saham" -> 30;
            case "Obligasi" -> 50;
            case "Pasar Uang" -> 20;
            default -> null;
        };
    }

    private List<RecommendedProduct> getRecommendedProductsByCategoryId(Long categoryId) {
        try {
            List<ProductResponse> products = productClient.getProductByCategoryId(categoryId);

            return products.stream()
                    .map(p -> RecommendedProduct.builder()
                            .productId(p.getProductId())
                            .productName(p.getProductName())
                            .categoryId(p.getCategoryId())
                            .productRate(p.getProductRate())
                            .productPrice(p.getProductPrice())
                            .build())
                    .toList();
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to get recommended products", e);
        }
    }

    @Override
    @Transactional
    public UpdateProgressResponse updateProgress(Long goalId, UpdateProgressRequest request) throws JsonProcessingException {
        FinancialGoal goal = financialGoalRepository.findById(goalId)
                .orElseThrow(() -> new GoalNotFoundException("Goal not found"));

        try {
            // Update the current amount if provided
            if (request.getCurrentAmount() != null) {
                goal.setCurrentAmount(request.getCurrentAmount());
            }

            // Update the insight message if provided
            if (request.getInsightMessage() != null) {
                goal.setInsightMessage(request.getInsightMessage());
            }
            goal.setUpdatedDate(new Date());

            // Log the audit trail
            auditTrailsService.logsAuditTrails(
                    GeneralConstant.LOG_ACTIVITY_UPDATE_PROGRESS,
                    mapper.writeValueAsString(goalId),
                    mapper.writeValueAsString(request),
                    "Update Progress After Transaction"
            );

            financialGoalRepository.save(goal);

            UpdateProgressResponse response = UpdateProgressResponse.builder()
                    .goalId(goal.getGoalId())
                    .currentAmount(goal.getCurrentAmount())
                    .insightMessage(goal.getInsightMessage())
                    .updatedDate(goal.getUpdatedDate())
                    .build();
            return response;

        } catch (Exception e) {
            throw new RuntimeException("Failed to update progress", e);
        }
    }

}