package com.product.app.client.dto;


import lombok.Data;

import java.util.Date;

@Data
public class AuditTrailsRequest {
    private String action;
    private String description;
    private Date date;
    private String request;
    private String response;
}
