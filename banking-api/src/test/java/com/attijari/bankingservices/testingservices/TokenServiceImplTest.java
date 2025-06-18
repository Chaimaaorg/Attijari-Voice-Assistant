package com.attijari.bankingservices.testingservices;

import com.attijari.bankingservices.models.Token;
import com.attijari.bankingservices.repositories.TokenRepository;
import com.attijari.bankingservices.services.implementations.TokenServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TokenServiceImplTest {

    @Mock
    private TokenRepository tokenRepository;

    @InjectMocks
    private TokenServiceImpl tokenService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testIsValid_TokenNotFound() {
        // Mock data
        String token = "invalid_token";

        when(tokenRepository.findByToken(token)).thenReturn(Optional.empty());

        // Call the method
        boolean isValid = tokenService.isValid(token);

        // Verify
        assertFalse(isValid);
    }

    @Test
    void testIsValid_TokenFound_NotLoggedOut() {
        // Mock data
        String token = "valid_token";
        Token validToken = new Token();
        validToken.setLoggedOut(false);

        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(validToken));

        // Call the method
        boolean isValid = tokenService.isValid(token);

        // Verify
        assertTrue(isValid);
    }

    @Test
    void testIsValid_TokenFound_LoggedOut() {
        // Mock data
        String token = "logged_out_token";
        Token loggedOutToken = new Token();
        loggedOutToken.setLoggedOut(true);

        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(loggedOutToken));

        // Call the method
        boolean isValid = tokenService.isValid(token);

        // Verify
        assertFalse(isValid);
    }
}
