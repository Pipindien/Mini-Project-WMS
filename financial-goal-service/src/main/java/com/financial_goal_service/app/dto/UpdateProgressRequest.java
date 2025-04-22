package com.financial_goal_service.app.dto;

import lombok.Data;

@Data
public class UpdateProgressRequest {
    private Double currentAmount;
    private String insightMessage;
}
