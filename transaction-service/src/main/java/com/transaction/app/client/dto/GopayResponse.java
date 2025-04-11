package com.transaction.app.client.dto;

import lombok.Data;

@Data
public class GopayResponse {
    private Double amount;
    private String status;
    private String phone;
    private Long custId;
    private String token;
}
