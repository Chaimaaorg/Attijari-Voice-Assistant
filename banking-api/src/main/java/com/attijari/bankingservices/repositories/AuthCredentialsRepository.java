package com.attijari.bankingservices.repositories;
import com.attijari.bankingservices.models.AuthCredentials;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthCredentialsRepository extends JpaRepository<AuthCredentials, Integer> {

    Optional<AuthCredentials> findByUsername(String username);
}