package com.transaction.app.client.dto;

import lombok.Data;

@Data
public class UsersResponse {
    private Long custId;
    private String fullName;
    private String email;
    private String password;
    private String phone;
    private Double balance;
    private String token;
}
