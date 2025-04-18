package com.transaction.app.dto.portosum;

import com.transaction.app.entity.PortfolioProductDetail;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class PortfolioSummaryResponse {
    private Long portoId;

    private Long goalId;
    private Long custId;
    private Double totalInvestment;
    private Double estimatedReturn;
    private Double totalProfit;
    private double returnPercentage;
    private Map<String, Double> categoryAllocation; // e.g., "Saham" => 60.0
    private List<PortfolioProductDetailResponse> portfolioProductDetails;
}
