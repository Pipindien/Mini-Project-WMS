package com.users.app.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class AuditTrailsRequest {

    private String action;
    private String description;
    private Date date;
    private String request;
    private String response;
}
