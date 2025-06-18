package com.attijari.bankingservices.repositories;

import com.attijari.bankingservices.models.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Integer> {


    @Query("""
                select t from Token t inner join AuthCredentials u on t.auth_credentials.id = u.id
                            where t.auth_credentials.id = :auth_id and t.loggedOut = false
            """)
    List<Token> findAllTokensByAuthCredentials(Integer auth_id);

    Optional<Token> findByToken(String token);
}
