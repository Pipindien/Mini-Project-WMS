package com.users.app.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.users.app.dto.LoginRequest;
import com.users.app.dto.LoginResponse;
import com.users.app.service.LoginService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
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
    public ResponseEntity<LoginResponse> getProfileFromToken(@RequestHeader String token) throws JsonProcessingException{
        LoginResponse loginResponse = loginService.checkToken(token);

        return ResponseEntity.ok(loginResponse);
    }


    @PutMapping("/balance/{custId}")
    public ResponseEntity<String> updateBalance(
            @PathVariable Long custId,
            @RequestBody Map<String, Object> requestBody,
            @RequestHeader String token
    ) {
        System.out.println("Received Token: " + token); // Cek token yang diterima

        double amount = ((Number) requestBody.getOrDefault("amount", 0.0)).doubleValue();
        boolean isAddition = (boolean) requestBody.getOrDefault("isAddition", true); // Default: tambah saldo

        loginService.updateUserBalance(custId, amount, isAddition);

        String message = isAddition ? "Saldo berhasil ditambahkan" : "Saldo berhasil dikurangi";
        return ResponseEntity.ok(message);
    }


}
