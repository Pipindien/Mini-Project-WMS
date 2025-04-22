package com.financial_goal_service.app.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RecommendedProduct {
    private Long productId;
    private Long categoryId;
    private String productName;
    private Double productPrice;
    private Double productRate;
    // tambahkan field lain sesuai kebutuhan (misal returnRate, issuer, dll)
}

