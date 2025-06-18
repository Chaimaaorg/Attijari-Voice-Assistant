package com.attijari.bankingservices.services.implementations;

import com.attijari.bankingservices.repositories.TokenRepository;
import com.attijari.bankingservices.services.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TokenServiceImpl implements TokenService {

    private final TokenRepository tokenRepository;

    @Autowired
    public TokenServiceImpl(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Override
    public boolean isValid(String token) {
        return tokenRepository.findByToken(token)
                .map(t -> !t.isLoggedOut())
                .orElse(false);
    }
}
