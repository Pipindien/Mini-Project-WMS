package com.transaction.app.advice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@AllArgsConstructor
@Data
public class ErrorResponse {
    private int status;
    private String error;
    private String message;
    private Date timestamp;
}
