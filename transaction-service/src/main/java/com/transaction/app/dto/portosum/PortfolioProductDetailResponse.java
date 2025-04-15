package com.transaction.app.dto.portosum;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PortfolioProductDetailResponse {
    private Long idPortoDetail;
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
