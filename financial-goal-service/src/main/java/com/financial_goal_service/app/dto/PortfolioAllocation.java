package com.financial_goal_service.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PortfolioAllocation {
    private String category;
    private Integer percentage;
}
