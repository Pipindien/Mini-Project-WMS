package com.financial_goal_service.app.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class FinancialGoalRequest {
    @NotBlank(message = "Goal name can't be empty")
    private String goalName;

    @NotNull(message = "Target amount can't be empty")
    @Positive(message = "Target amount must be positive")
    private Double targetAmount;

    @NotNull(message = "Target date can't be empty")
    @Future(message = "Target date must be in the future")
    private Date targetDate;

/*    @NotBlank(message = "Risk tolerance can't be empty")
    @Pattern(regexp = "Conservative|Moderate|Aggressive", message = "Risk tolerance must be Conservative, Moderate, or Aggressive")
    private String riskTolerance;*/
}
