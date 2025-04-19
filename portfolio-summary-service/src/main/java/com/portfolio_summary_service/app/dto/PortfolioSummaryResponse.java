package com.portfolio_summary_service.app.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class PortfolioSummaryResponse {
    private Long goalId;
    private Long custId;
    private Double totalInvestment;
    private Double estimatedReturn;
    private Double totalProfit;
    private List<ProductSummaryResponse> productSummaryResponses;
}
