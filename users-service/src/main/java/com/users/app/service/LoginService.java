package com.users.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.users.app.dto.LoginRequest;
import com.users.app.dto.LoginResponse;
import com.users.app.entity.Users;

public interface LoginService {
    LoginResponse login(LoginRequest loginRequest) throws JsonProcessingException;

    LoginResponse registerasi(LoginRequest loginRequest) throws JsonProcessingException;

    LoginResponse checkToken(String token) throws JsonProcessingException;

    void updateUserBalance(Long custId, Double amount, boolean isAddition);
}
