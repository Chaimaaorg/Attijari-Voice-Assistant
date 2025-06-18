package com.attijari.bankingservices.services.implementations;
import com.attijari.bankingservices.repositories.AuthCredentialsRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthCredentialsDetailsServiceImpl implements UserDetailsService {

    private final AuthCredentialsRepository repository;

    public AuthCredentialsDetailsServiceImpl(AuthCredentialsRepository repository) {
        this.repository = repository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByUsername(username)
                .orElseThrow(()-> new UsernameNotFoundException("User not found"));
    }
}
