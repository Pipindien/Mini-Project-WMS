package com.financial_goal_service.app.advice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RestApiResponse<T> {
    private int status;
    private String message;
    private LocalDateTime timestamp;
    private T data;
}
