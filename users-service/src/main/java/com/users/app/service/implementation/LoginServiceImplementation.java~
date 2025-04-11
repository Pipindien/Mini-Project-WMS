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
    private  UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
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
                    .role("USER")
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
            String username = jwtService.extractUsername(token);

            Users user = usersRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Username Not Found"));

            boolean isValid = jwtService.isTokenValid(token, user);
            if (!isValid) {
                throw new RuntimeException("Token is invalid or expired.");
            }

            auditTrailsService.insertAuditTrails(AuditTrailsRequest.builder()
                    .action(GeneralConstant.LOG_ACVITIY_CHECK_TOKEN)
                    .description("Token Validated")
                    .date(new Date())
                    .request(objectMapper.writeValueAsString(token))
                    .response(objectMapper.writeValueAsString(user.getCustId()))
                    .build());

            return LoginResponse.builder()
                    .token(token)
                    .build();

        } catch (Exception ex) {
            auditTrailsService.insertAuditTrails(AuditTrailsRequest.builder()
                    .action(GeneralConstant.LOG_ACVITIY_CHECK_TOKEN)
                    .description("Token Invalid")
                    .date(new Date())
                    .request(objectMapper.writeValueAsString(token))
                    .response(ex.getMessage())
                    .build());

            throw new TokenExpiredException("Token Invalid or Expired.");
        }
    }

    public Users updateProfile(String token, Users updatedProfile) throws JsonProcessingException {
        try {
            String username = jwtService.extractUsername(token);

            Users existingUser = usersRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Username Not Found"));

            existingUser.setFullName(updatedProfile.getFullName());
            existingUser.setEmail(updatedProfile.getEmail());
            existingUser.setPhone(updatedProfile.getPhone());
            existingUser.setAge(updatedProfile.getAge());
            existingUser.setSalary(updatedProfile.getSalary());

            Users savedUser = usersRepository.save(existingUser);

            auditTrailsService.insertAuditTrails(AuditTrailsRequest.builder()
                    .action(GeneralConstant.LOG_ACVITIY_UPDATE)
                    .description("User profile updated successfully")
                    .date(new Date())
                    .request(objectMapper.writeValueAsString(updatedProfile))
                    .response(objectMapper.writeValueAsString(savedUser))
                    .build());

            return savedUser;

        } catch (Exception ex) {
            auditTrailsService.insertAuditTrails(AuditTrailsRequest.builder()
                    .action(GeneralConstant.LOG_ACVITIY_UPDATE)
                    .description("Failed to update profile")
                    .date(new Date())
                    .request(objectMapper.writeValueAsString(updatedProfile))
                    .response(ex.getMessage())
                    .build());

            throw ex;
        }
    }


}
