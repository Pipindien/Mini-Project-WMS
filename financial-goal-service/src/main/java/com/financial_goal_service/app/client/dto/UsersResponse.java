package com.financial_goal_service.app.client.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsersResponse {
    private Long custId;
    private String token;
    private Integer age;
    private Double salary;

}
