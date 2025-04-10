package com.users.app.dto;

import lombok.Data;

import java.util.Date;

@Data
public class AuditTrailsResponse {

    private String action;
    private String description;
    private Date date;
    private String request;
    private String response;
}
