package com.users.app.service.implementation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.users.app.advice.exception.*;
import com.users.app.dto.AuditTrailsRequest;
import com.users.app.dto.LoginRequest;
import com.users.app.dto.LoginResponse;
import com.users.app.entity.Users;
import com.users.app.repository.UsersRepository;
import com.users.app.service.AuditTrailsService;
import com.users.app.service.JWTService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LoginServiceImplementationTest {

    @InjectMocks
    private LoginServiceImplementation loginService;

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private AuditTrailsService auditTrailsService;

    @Mock
    private JWTService jwtService;

    private LoginRequest validRequest;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        validRequest = LoginRequest.builder()
                .username("pipin")
                .fullName("Pipin Maulana")
                .email("pipin@mail.com")
                .password("securepass")
                .phone("+628123456789")
                .age(30)
                .salary(5000.0)
                .build();

        // Lenient karena dipakai di banyak test tapi tidak semuanya
        lenient().when(objectMapper.writeValueAsString(any())).thenReturn("{}");
    }

    @Test
    public void testRegisterasi_Success() throws Exception {
        when(usersRepository.findByPhone(validRequest.getPhone())).thenReturn(Optional.empty());
        when(usersRepository.findByEmail(validRequest.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(validRequest.getPassword())).thenReturn("encodedpass");

        Users savedUser = Users.builder()
                .custId(1L)
                .username("pipin")
                .fullName("Pipin Maulana")
                .email("pipin@mail.com")
                .phone("+628123456789")
                .age(30)
                .salary(5000.0)
                .password("encodedpass")
                .role("USER")
                .build();

        when(usersRepository.save(any())).thenReturn(savedUser);
        when(jwtService.generateToken("pipin", 1L)).thenReturn("mockedToken");

        LoginResponse response = loginService.registerasi(validRequest);

        assertNotNull(response);
        assertEquals("mockedToken", response.getToken());
        verify(auditTrailsService).insertAuditTrails(any(AuditTrailsRequest.class));
    }

    @Test(expected = PhoneAlreadyRegisteredException.class)
    public void testRegisterasi_PhoneAlreadyExists() throws Exception {
        when(usersRepository.findByPhone(validRequest.getPhone())).thenReturn(Optional.of(new Users()));

        loginService.registerasi(validRequest);
    }

    @Test(expected = EmailAlreadyRegisteredException.class)
    public void testRegisterasi_EmailAlreadyExists() throws Exception {
        when(usersRepository.findByPhone(validRequest.getPhone())).thenReturn(Optional.empty());
        when(usersRepository.findByEmail(validRequest.getEmail())).thenReturn(Optional.of(new Users()));

        loginService.registerasi(validRequest);
    }

    @Test
    public void testLogin_Success() throws Exception {
        Users user = Users.builder()
                .username("pipin")
                .custId(1L)
                .password("encodedPassword")
                .age(30)
                .salary(5000.0)
                .role("USER")
                .build();

        LoginRequest loginRequest = LoginRequest.builder()
                .username("pipin")
                .password("securepass")
                .build();

        when(usersRepository.findByUsername("pipin")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(eq("securepass"), eq("encodedPassword"))).thenReturn(true);
        when(jwtService.generateToken("pipin", 1L)).thenReturn("mocked-token");

        LoginResponse response = loginService.login(loginRequest);

        assertNotNull(response);
        assertEquals("mocked-token", response.getToken());
        assertEquals(1L, response.getCustId().longValue());
        assertEquals(30, response.getAge().intValue());
        assertEquals(5000.0, response.getSalary(), 0.01);
        assertEquals("USER", response.getRole());
    }

    @Test(expected = UsernameNotFoundException.class)
    public void testLogin_UsernameNotFound() throws Exception {
        when(usersRepository.findByUsername("wrong")).thenReturn(Optional.empty());

        LoginRequest req = LoginRequest.builder()
                .username("wrong")
                .password("any")
                .build();

        loginService.login(req);
    }

    @Test(expected = PasswordInvalidException.class)
    public void testLogin_PasswordInvalid() throws Exception {
        Users user = Users.builder()
                .username("pipin")
                .password("encodedpass")
                .build();

        when(usersRepository.findByUsername("pipin")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encodedpass")).thenReturn(false);

        LoginRequest req = LoginRequest.builder()
                .username("pipin")
                .password("wrong")
                .build();

        loginService.login(req);
    }

    @Test
    public void testCheckToken_Valid() throws Exception {
        String token = "valid.token";
        Users user = Users.builder()
                .username("pipin")
                .custId(1L)
                .salary(5000.0)
                .age(30)
                .build();

        when(jwtService.extractUsername(token)).thenReturn("pipin");
        when(usersRepository.findByUsername("pipin")).thenReturn(Optional.of(user));
        when(jwtService.isTokenValid(token, user)).thenReturn(true);

        LoginResponse response = loginService.checkToken(token);

        assertNotNull(response);
        assertEquals(Long.valueOf(1L), response.getCustId());
        assertEquals(token, response.getToken());
    }

    @Test(expected = TokenExpiredException.class)
    public void testCheckToken_InvalidToken() throws Exception {
        String token = "invalid.token";
        Users user = Users.builder().username("pipin").build();

        when(jwtService.extractUsername(token)).thenReturn("pipin");
        when(usersRepository.findByUsername("pipin")).thenReturn(Optional.of(user));
        when(jwtService.isTokenValid(token, user)).thenReturn(false);

        loginService.checkToken(token);
    }
}
