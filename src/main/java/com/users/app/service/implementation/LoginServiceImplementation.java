package com.users.app.service.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.users.app.constant.GeneralConstant;
import com.users.app.dto.AuditTrailsRequest;
import com.users.app.dto.LoginRequest;
import com.users.app.dto.LoginResponse;
import com.users.app.entity.Token;
import com.users.app.entity.Users;
import com.users.app.repository.TokenRepository;
import com.users.app.repository.UsersRepository;
import com.users.app.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoginServiceImplementation implements LoginService {

    private final UsersRepository usersRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;
    private final AuditTrailsServiceImplementation auditTrailsServiceImplementation;

    @Override
    public LoginResponse registerasi(LoginRequest loginRequest) throws JsonProcessingException {
        Users users = Users.builder()
                .fullName(loginRequest.getFullName())
                .email(loginRequest.getEmail())
                .phone(loginRequest.getPhone())
                .username(loginRequest.getUsername())
                .password(passwordEncoder.encode(loginRequest.getPassword()))
                .role("USER") // Default role
                .build();

        Users savedUser = usersRepository.save(users);

        // Generate JWT Token
        String jwtToken = UUID.randomUUID().toString();
        Token token = Token.builder()
                .jwtToken(jwtToken)
                .expiredDate(new Date(System.currentTimeMillis() + 86400000)) // 1 day expiry
                .user(savedUser)
                .build();
        tokenRepository.save(token);

        LoginResponse response = LoginResponse.builder()
                .token(jwtToken)
                .build();

        auditTrailsServiceImplementation.insertAuditTrails(AuditTrailsRequest.builder()
                .action(GeneralConstant.LOG_ACVITIY_REGISTER)
                .description("Success Register")
                .date(new Date())
                .request(objectMapper.writeValueAsString(loginRequest))
                .response(objectMapper.writeValueAsString(response))
                .build());

        return response;
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) throws JsonProcessingException {
        Users user = usersRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("Username tidak ditemukan"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Password salah");
        }

        tokenRepository.findByUser(user).ifPresent(tokenRepository::delete);

        String newToken = UUID.randomUUID().toString();
        Token token = Token.builder()
                .jwtToken(newToken)
                .expiredDate(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .user(user)
                .build();

        tokenRepository.save(token);

        auditTrailsServiceImplementation.insertAuditTrails(AuditTrailsRequest.builder()
                .action(GeneralConstant.LOG_ACVITIY_LOGIN)
                .description("Success Login")
                .date(new Date())
                .request(objectMapper.writeValueAsString(loginRequest))
                .response(objectMapper.writeValueAsString(token))
                .build());

        return LoginResponse.builder()
                .token(token.getJwtToken()) // Ambil token dari tabel token
                .build();
    }

    @Override
    public Boolean checkToken(String token) throws JsonProcessingException {
        Token foundToken = tokenRepository.findByJwtToken(token)
                .orElseThrow(() -> new RuntimeException("Token Not Found"));

        auditTrailsServiceImplementation.insertAuditTrails(AuditTrailsRequest.builder()
                .action(GeneralConstant.LOG_ACVITIY_CHECK_TOKEN)
                .description("Token Validated")
                .date(new Date())
                .request(objectMapper.writeValueAsString(token))
                .response(objectMapper.writeValueAsString(foundToken))
                .build());

        return true;
    }
}
