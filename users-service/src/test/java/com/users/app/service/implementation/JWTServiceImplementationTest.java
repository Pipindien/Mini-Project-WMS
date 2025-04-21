package com.users.app.service.implementation;

import com.users.app.entity.Users;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Field;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JWTServiceImplementationTest {

    @InjectMocks
    private JWTServiceImplementation jwtService;

    @Mock
    private Users userDetails;

    private String secretKey = Base64.getEncoder().encodeToString("YourSuperSecretKey1234567890".getBytes());
    private String token;

    @Before
    public void setUp() throws Exception {
        // Gunakan key yang kuat
        String strongSecret = "ThisIsASecureSecretKeyThatHasAtLeast32Bytes!";
        secretKey = Base64.getEncoder().encodeToString(strongSecret.getBytes());

        // Inject ke jwtService
        Field secretField = JWTServiceImplementation.class.getDeclaredField("secretKey");
        secretField.setAccessible(true);
        secretField.set(jwtService, secretKey);

        when(userDetails.getUsername()).thenReturn("pipin");

        token = jwtService.generateToken("pipin", 1L);
    }


    @Test
    public void testGenerateToken_ShouldReturnValidToken() {
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    public void testExtractUsername_ShouldReturnCorrectUsername() {
        String username = jwtService.extractUsername(token);
        assertEquals("pipin", username);
    }

    @Test
    public void testExtractCustId_ShouldReturnCorrectCustomerId() {
        Long custId = jwtService.extractCustId(token);
        assertEquals(Long.valueOf(1L), custId);
    }

    @Test
    public void testIsTokenValid_ShouldReturnTrueForValidToken() {
        boolean isValid = jwtService.isTokenValid(token, userDetails);
        assertTrue(isValid);
    }

    @Test
    public void testIsTokenExpired_ShouldReturnFalseForFreshToken() {
        boolean isExpired = jwtService.isTokenExpired(token);
        assertFalse(isExpired);
    }

    @Test
    public void testIsTokenExpired_ShouldReturnTrueForExpiredToken() throws Exception {
        // Generate expired token
        Date now = new Date();
        Date expired = new Date(now.getTime() - 1000); // 1 detik yang lalu (expired)

        Key key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey));
        String expiredToken = Jwts.builder()
                .setSubject("pipin")
                .claim("custId", 1L)
                .setIssuedAt(now)
                .setExpiration(expired)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        boolean isExpired;
        try {
            isExpired = jwtService.isTokenExpired(expiredToken);
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            isExpired = true; // kalau exception muncul, berarti memang expired
        }

        assertTrue(isExpired);
    }


    @Test
    public void testExtractClaim_ShouldReturnSubjectClaim() {
        String subject = jwtService.extractClaim(token, Claims::getSubject);
        assertEquals("pipin", subject);
    }

    @Test
    public void testCreateTokenViaPublicAPI_ShouldReturnNonEmptyToken() {
        String username = "pipin";
        Long custId = 1L;

        String newToken = jwtService.generateToken(username, custId);
        assertNotNull(newToken);
        assertFalse(newToken.isEmpty());
    }
}
