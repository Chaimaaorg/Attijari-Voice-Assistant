package com.attijari.bankingservices.testingservices;

import com.attijari.bankingservices.models.AuthCredentials;
import com.attijari.bankingservices.models.Token;
import com.attijari.bankingservices.repositories.TokenRepository;
import com.attijari.bankingservices.services.implementations.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private JwtService jwtService;

    @Mock
    private TokenRepository tokenRepository;

    private final String SECRET_KEY = "4bb6d1dfbafb64a681139d1586b6f1160d18159afd57c8c79136d7490630407c";
    private final String USERNAME = "testuser";
    private final String TOKEN = generateToken(USERNAME);

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(tokenRepository);
    }

    @Test
    void testExtractUsername() {
        String extractedUsername = jwtService.extractUsername(TOKEN);
        assertTrue(USERNAME.equals(extractedUsername));
    }

    @Test
    void testIsValidToken() {
        UserDetails userDetails = User.withUsername(USERNAME).password("password").roles("USER").build();
        Token validToken = new Token();
        validToken.setToken(TOKEN);
        validToken.setLoggedOut(false);
        when(tokenRepository.findByToken(TOKEN)).thenReturn(Optional.of(validToken));

        boolean isValid = jwtService.isValid(TOKEN, userDetails);
        assertTrue(isValid);
    }

    @Test
    void testIsInvalidToken() {
        UserDetails userDetails = User.withUsername(USERNAME).password("password").roles("USER").build();
        when(tokenRepository.findByToken(TOKEN)).thenReturn(Optional.empty());

        boolean isValid = jwtService.isValid(TOKEN, userDetails);
        assertFalse(isValid);
    }

    private String generateToken(String username) {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(SECRET_KEY));
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000))
                .signWith(key)
                .compact();
    }

}