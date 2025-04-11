package com.financial_goal_service.app.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class SuggestedPortfolioResponse {
    private Long goalId;
    private List<PortfolioAllocation> suggestedPortfolio;
    private Map<String, List<RecommendedProduct>> recommendedProducts;

}
