package com.financial_goal_service.app.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class UpdateProgressResponse {
    private Long goalId;
    private Double currentAmount;
    private String insightMessage;
    private Date updatedDate;
}
