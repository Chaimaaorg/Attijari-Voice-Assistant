package com.attijari.bankingservices.controllers;

import com.attijari.bankingservices.models.AuthCredentials;
import com.attijari.bankingservices.models.AuthenticationResponse;
import com.attijari.bankingservices.services.implementations.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name="Authentification",description = "Contrôleur de la distribution des tokens pour l'API.")
public class AuthenticationController {

    private final AuthenticationService authService;

    public AuthenticationController(AuthenticationService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody AuthCredentials request
    ) {
        return ResponseEntity.ok(authService.register(request));
    }
    @PostMapping("/login")
    @Operation(summary = "Méthode pour récupérer le token pour un utilisateur donné de l'API")
    public ResponseEntity<AuthenticationResponse> login(
            @RequestBody AuthCredentials request
    ) {
        return ResponseEntity.ok(authService.authenticate(request));
    }
}