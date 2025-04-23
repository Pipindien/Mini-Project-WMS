package com.users.app.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
public class LoginResponse {
    private String token;
    private Long custId;
    private String fullname;
    private String phone;
    private String email;
    private Integer age;
    private Double salary;
    private Double balance;
    private String role;
}
