package com.users.app.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.users.app.dto.LoginRequest;
import com.users.app.dto.LoginResponse;
import com.users.app.entity.Users;
import com.users.app.service.LoginService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) throws JsonProcessingException {
        return ResponseEntity.ok(loginService.login(loginRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody LoginRequest loginRequest) throws JsonProcessingException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(loginService.registerasi(loginRequest));
    }

    @GetMapping("/")
    public ResponseEntity<LoginResponse> getUser(@RequestHeader String token) throws JsonProcessingException {
        LoginResponse loginResponse = loginService.checkToken(token);
        return ResponseEntity.ok(loginResponse);
    }

    @PutMapping("/profile")
    public ResponseEntity<Users> updateProfile(
            @RequestHeader("Authorization") String token,
          @Valid @RequestBody Users updatedProfile) throws JsonProcessingException {

        String jwtToken = token.replace("Bearer ", "");

        Users updated = loginService.updateProfile(jwtToken, updatedProfile);
        return ResponseEntity.ok(updated);
    }
}
