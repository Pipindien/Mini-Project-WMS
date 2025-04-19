package com.portfolio_summary_service.app.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductSummaryResponse {
        private Long productId;
        private String productName;
        private Long categoryId;
        private Integer totalLot;
        private Double buyPrice;
        private Double productRate;
        private Double investmentAmount;
        private Double estimatedReturn;
        private Double profit;
}