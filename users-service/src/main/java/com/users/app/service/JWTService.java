package com.users.app.service;

import com.users.app.entity.Users;

public interface JWTService {
    String generateToken(String username, Long custId);
    boolean isTokenValid(String token, Users userDetails);
    String extractUsername(String token);
    Long extractCustId(String token);
}

