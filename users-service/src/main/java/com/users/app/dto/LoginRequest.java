package com.users.app.dto;

import lombok.Builder;
import lombok.Data;



@Builder
@Data
public class LoginRequest {
    private String username;
    private String fullName;
    private String email;
    private String password;
    private String phone;
    private Integer age;
    private Double salary;
    private String token;
}
