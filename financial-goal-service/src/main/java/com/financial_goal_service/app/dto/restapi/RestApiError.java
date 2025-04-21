package com.financial_goal_service.app.dto.restapi;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum RestApiError {
    GOAL_NOT_FOUND(HttpStatus.BAD_REQUEST.value(), "ERROR NOT FOUND"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "INTERNAL SERVER ERROR"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST.value(), "Invalid FinancialGoal Id Format" );


    private final int code;
    private final String message;
}
