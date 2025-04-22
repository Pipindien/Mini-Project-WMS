package com.financial_goal_service.app.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class FinancialGoalResponse {

    private Long goalId;
    private Long custId;
    private String goalName;
    private Double targetAmount;
    private Double currentAmount;
    private Date targetDate;
    private String riskTolerance;
    private String status;
    private Date createdDate;
    private Date UpdatedDate;
    private String insightMessage;

}
