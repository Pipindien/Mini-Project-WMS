package com.transaction.app.dto.portosum;

import com.transaction.app.entity.PortfolioProductDetail;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PortfolioSummaryResponse {
    private Long portoId;

    private Long goalId;
    private Long custId;
    private Double totalInvestment;
    private Double estimatedReturn;
    private Double totalProfit;
    private List<PortfolioProductDetailResponse> portfolioProductDetails;
}
