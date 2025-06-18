package com.attijari.bankingservices.services.implementations;

import com.attijari.bankingservices.models.AuthCredentials;
import com.attijari.bankingservices.models.AuthenticationResponse;
import com.attijari.bankingservices.models.Token;
import com.attijari.bankingservices.repositories.AuthCredentialsRepository;
import com.attijari.bankingservices.repositories.TokenRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthenticationService {

    private final AuthCredentialsRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private final TokenRepository tokenRepository;

    private final AuthenticationManager authenticationManager;

    public AuthenticationService(AuthCredentialsRepository repository, PasswordEncoder passwordEncoder, JwtService jwtService, TokenRepository tokenRepository, AuthenticationManager authenticationManager) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.tokenRepository = tokenRepository;
        this.authenticationManager = authenticationManager;
    }


    public AuthenticationResponse register(AuthCredentials request) {

        // check if authCredentials already exist. if exist than authenticate the authCredentials
        if(repository.findByUsername(request.getUsername()).isPresent()) {
            return new AuthenticationResponse(null, "authCredentials already exist");
        }

        AuthCredentials authCredentials = new AuthCredentials();
        authCredentials.setUsername(request.getUsername());
        authCredentials.setPassword(passwordEncoder.encode(request.getPassword()));


        authCredentials.setRole(request.getRole());

        authCredentials = repository.save(authCredentials);

        String jwt = jwtService.generateToken(authCredentials);

        saveauthCredentialsToken(jwt, authCredentials);

        return new AuthenticationResponse(jwt, "authCredentials registration was successful");

    }

    public AuthenticationResponse authenticate(AuthCredentials request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        AuthCredentials authCredentials = repository.findByUsername(request.getUsername()).orElseThrow();
        String jwt = jwtService.generateToken(authCredentials);

        revokeAllTokenByauthCredentials(authCredentials);
        saveauthCredentialsToken(jwt, authCredentials);

        return new AuthenticationResponse(jwt, "authCredentials login was successful");

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
}
