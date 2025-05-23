package com.users.app.service.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.users.app.advice.exception.*;
import com.users.app.constant.GeneralConstant;
import com.users.app.dto.AuditTrailsRequest;
import com.users.app.dto.LoginRequest;
import com.users.app.dto.LoginResponse;
import com.users.app.entity.Users;
import com.users.app.repository.UsersRepository;
import com.users.app.service.AuditTrailsService;
import com.users.app.service.JWTService;
import com.users.app.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class LoginServiceImplementation implements LoginService {

    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;
    @Autowired
    private AuditTrailsService auditTrailsService;

    @Autowired
    private JWTService jwtService;

    @Override
    public LoginResponse registerasi(LoginRequest loginRequest) throws JsonProcessingException {
        try {
            if (usersRepository.findByPhone(loginRequest.getPhone()).isPresent()) {
                throw new PhoneAlreadyRegisteredException("Phone Already Register.");
            }

            if (usersRepository.findByEmail(loginRequest.getEmail()).isPresent()) {
                throw new EmailAlreadyRegisteredException("Email Already Register.");
            }

            Users users = Users.builder()
                    .fullName(loginRequest.getFullName())
                    .email(loginRequest.getEmail())
                    .phone(loginRequest.getPhone())
                    .age(loginRequest.getAge())
                    .salary(loginRequest.getSalary())
                    .username(loginRequest.getUsername())
                    .password(passwordEncoder.encode(loginRequest.getPassword()))
                    .createdDate(new Date())
                    .role("USER")
                    .balance(0.0) // Initial balance set to 0
                    .build();

            Users savedUser = usersRepository.save(users);

            String jwtToken = jwtService.generateToken(savedUser.getUsername(), savedUser.getCustId());

            LoginResponse response = LoginResponse.builder()
                    .token(jwtToken)
                    .build();

            auditTrailsService.insertAuditTrails(AuditTrailsRequest.builder()
                    .action(GeneralConstant.LOG_ACVITIY_REGISTER)
                    .description("Success Register")
                    .date(new Date())
                    .request(objectMapper.writeValueAsString(loginRequest))
                    .response(objectMapper.writeValueAsString(response))
                    .build());

            return response;

        } catch (PhoneAlreadyRegisteredException | EmailAlreadyRegisteredException ex) {
            auditTrailsService.insertAuditTrails(AuditTrailsRequest.builder()
                    .action(GeneralConstant.LOG_ACVITIY_REGISTER)
                    .description("Failed Register")
                    .date(new Date())
                    .request(objectMapper.writeValueAsString(loginRequest))
                    .response(ex.getMessage())
                    .build());
            throw ex;
        }
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) throws JsonProcessingException {
        try {
            Users user = usersRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("Username Not Found"));

            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                throw new PasswordInvalidException("Password Invalid");
            }

            String jwtToken = jwtService.generateToken(user.getUsername(), user.getCustId());

            auditTrailsService.insertAuditTrails(AuditTrailsRequest.builder()
                    .action(GeneralConstant.LOG_ACVITIY_LOGIN)
                    .description("Success Login")
                    .date(new Date())
                    .request(objectMapper.writeValueAsString(loginRequest))
                    .response(objectMapper.writeValueAsString(jwtToken))
                    .build());

            return LoginResponse.builder()
                    .token(jwtToken)
                    .custId(user.getCustId())
                    .salary(user.getSalary())
                    .age(user.getAge())
                    .role(user.getRole())
                    .build();

        } catch (UsernameNotFoundException | PasswordInvalidException ex) {
            auditTrailsService.insertAuditTrails(AuditTrailsRequest.builder()
                    .action(GeneralConstant.LOG_ACVITIY_LOGIN)
                    .description("Failed Login")
                    .date(new Date())
                    .request(objectMapper.writeValueAsString(loginRequest))
                    .response(ex.getMessage())
                    .build());
            throw ex;
        }
    }

    @Override
    public LoginResponse checkToken(String token) throws JsonProcessingException {
        try {
            String username = jwtService.extractUsername(token); // Mengekstrak username dari token

            Users user = usersRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Username Not Found"));

            boolean isValid = jwtService.isTokenValid(token, user); // Verifikasi token
            if (!isValid) {
                throw new RuntimeException("Token is invalid or expired.");
            }

            // Setelah token tervalidasi, kembalikan data pengguna dalam bentuk LoginResponse
            return LoginResponse.builder()
                    .token(token)
                    .fullname(user.getFullName())
                    .email(user.getEmail())
                    .phone(user.getPhone())
                    .custId(user.getCustId())
                    .age(user.getAge())
                    .salary(user.getSalary())
                    .balance(user.getBalance())
                    .role(user.getRole())
                    .build();

        } catch (Exception ex) {
            throw new TokenExpiredException("Token Invalid or Expired.");
        }
    }

    public void updateUserBalance(Long custId, Double amount, boolean isAddition) {
        Users user = usersRepository.findById(custId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + custId));

        if (user.getBalance() == null) {
            user.setBalance(0.0);
        }

        if (!isAddition) {
            if (user.getBalance() < amount) {
                throw new RuntimeException("Insufficient balance for this transaction.");
            }
            user.setBalance(user.getBalance() - amount);
        } else {
            user.setBalance(user.getBalance() + amount);
        }

        usersRepository.save(user);
    }


}
