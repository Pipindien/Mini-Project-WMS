package com.transaction.app.client.dto;

import lombok.Data;

@Data
public class UpdateProgressRequest {
    private Double currentAmount;
    private String insightMessage;

}
