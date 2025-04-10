package com.financial_goal_service.app.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Table(name = "financial_goal")
public class FinancialGoal {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long goalId;
    private Long custId;
    private String goalName;
    private Integer targetAmount;
    private Integer currentAmount;
    private Date targetDate;
    private String riskTolerance;
    private String status;
    private Date createdDate;
    private Date updatedDate;
    private Boolean deleted;
    private String insightMessage;
}
