package com.attijari.bankingservices.testingservices;

import com.attijari.bankingservices.models.AuthCredentials;
import com.attijari.bankingservices.models.AuthenticationResponse;
import com.attijari.bankingservices.models.Role;
import com.attijari.bankingservices.models.Token;
import com.attijari.bankingservices.repositories.AuthCredentialsRepository;
import com.attijari.bankingservices.repositories.TokenRepository;
import com.attijari.bankingservices.services.implementations.AuthenticationService;
import com.attijari.bankingservices.services.implementations.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {

    @Mock
    private AuthCredentialsRepository repository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private TokenRepository tokenRepository;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testRegister_NewCredentials_Success() {
        // Mock data
        AuthCredentials request = new AuthCredentials();
        request.setUsername("testuser");
        request.setPassword("testpassword");
        request.setRole(Role.valueOf("ADMIN"));

        when(repository.findByUsername(request.getUsername())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtService.generateToken(any())).thenReturn("generatedToken");

        // Call the method
        AuthenticationResponse response = authenticationService.register(request);

        // Verify the result
        assertNotNull(response.getToken());
        assertEquals("authCredentials registration was successful", response.getMessage());
        verify(repository, times(1)).findByUsername(request.getUsername());
        verify(repository, times(1)).save(any());
        verify(jwtService, times(1)).generateToken(any());
        verify(tokenRepository, times(1)).save(any());
    }

    @Test
    void testRegister_ExistingCredentials() {
        // Mock data
        AuthCredentials request = new AuthCredentials();
        request.setUsername("testuser");
        request.setPassword("testpassword");
        request.setRole(Role.valueOf("ADMIN"));

        when(repository.findByUsername(request.getUsername())).thenReturn(Optional.of(request));

        // Call the method
        AuthenticationResponse response = authenticationService.register(request);

        // Verify the result
        assertNull(response.getToken());
        assertEquals("authCredentials already exist", response.getMessage());
        verify(repository, times(1)).findByUsername(request.getUsername());
        verify(repository, never()).save(any());
        verify(jwtService, never()).generateToken(any());
        verify(tokenRepository, never()).save(any());
    }

    @Test
    void testAuthenticate_Success() {
        // Mock data
        AuthCredentials request = new AuthCredentials();
        request.setUsername("testuser");
        request.setPassword("testpassword");
        request.setRole(Role.valueOf("ADMIN"));

        AuthCredentials existingCredentials = new AuthCredentials();
        existingCredentials.setUsername("testuser");
        existingCredentials.setPassword("encodedPassword");

        when(repository.findByUsername(request.getUsername())).thenReturn(Optional.of(existingCredentials));
        when(jwtService.generateToken(any())).thenReturn("generatedToken");

        // Call the method
        AuthenticationResponse response = authenticationService.authenticate(request);

        // Verify the result
        assertNotNull(response.getToken());
        assertEquals("authCredentials login was successful", response.getMessage());
        verify(authenticationManager, times(1)).authenticate(any());
        verify(tokenRepository, times(1)).save(any());
        verify(tokenRepository, times(1)).findAllTokensByAuthCredentials(any());
    }

    @Test
    void testAuthenticate_InvalidCredentials() {
        // Mock data
        AuthCredentials request = new AuthCredentials();
        request.setUsername("testuser");
        request.setPassword("wrongpassword");
        request.setRole(Role.valueOf("ADMIN"));

        // Mock repository
        when(repository.findByUsername(request.getUsername())).thenReturn(Optional.empty());

        // Call the method and verify the exception
        assertThrows(NoSuchElementException.class, () -> {
            authenticationService.authenticate(request);
        });

        // Verify that repository.findByUsername is called once
        verify(repository, times(1)).findByUsername(request.getUsername());
    }





    private void revokeAllTokenByauthCredentials(AuthCredentials authCredentials) {
        List<Token> validTokens = tokenRepository.findAllTokensByAuthCredentials(authCredentials.getId());
        if(validTokens.isEmpty()) {
            return;
        }

        validTokens.forEach(t-> {
            t.setLoggedOut(true);
        });

        tokenRepository.saveAll(validTokens);
    }
    private void saveauthCredentialsToken(String jwt, AuthCredentials authCredentials) {
        Token token = new Token();
        token.setToken(jwt);
        token.setLoggedOut(false);
        token.setAuth_credentials(authCredentials);
        tokenRepository.save(token);
    }
    @Test
    void testRevokeAllTokenByAuthCredentials() {
        // Mock data
        AuthCredentials authCredentials = new AuthCredentials();
        authCredentials.setId(1);

        Token token1 = new Token();
        token1.setLoggedOut(false);
        token1.setAuth_credentials(authCredentials);

        Token token2 = new Token();
        token2.setLoggedOut(false);
        token2.setAuth_credentials(authCredentials);

        when(tokenRepository.findAllTokensByAuthCredentials(authCredentials.getId())).thenReturn(Collections.singletonList(token1));

        // Call the method
        revokeAllTokenByauthCredentials(authCredentials);

        // Verify the result
        assertTrue(token1.isLoggedOut());
        assertFalse(token2.isLoggedOut());
        verify(tokenRepository, times(1)).findAllTokensByAuthCredentials(authCredentials.getId());
        verify(tokenRepository, times(1)).saveAll(Collections.singletonList(token1));
    }

    @Test
    void testSaveAuthCredentialsToken() {
        // Mock data
        AuthCredentials authCredentials = new AuthCredentials();
        authCredentials.setId(1);
        String jwt = "generatedToken";

        // Call the method
        saveauthCredentialsToken(jwt, authCredentials);

        // Verify the result
        verify(tokenRepository, times(1)).save(any(Token.class));
    }
}
