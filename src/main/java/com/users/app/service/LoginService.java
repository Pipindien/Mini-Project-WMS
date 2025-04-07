package com.users.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.users.app.dto.LoginRequest;
import com.users.app.dto.LoginResponse;

public interface LoginService {
    LoginResponse login(LoginRequest loginRequest) throws JsonProcessingException;

    LoginResponse registerasi(LoginRequest loginRequest) throws JsonProcessingException;

    Boolean checkToken(String token) throws JsonProcessingException;
}
