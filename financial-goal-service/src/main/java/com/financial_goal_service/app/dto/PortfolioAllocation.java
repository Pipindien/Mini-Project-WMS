package com.financial_goal_service.app.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PortfolioAllocation {
    private String category;
    private Integer percentage;
}
