package com.attijari.bankingservices.testingservices;

import com.attijari.bankingservices.models.AuthCredentials;
import com.attijari.bankingservices.repositories.AuthCredentialsRepository;
import com.attijari.bankingservices.services.implementations.AuthCredentialsDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class AuthCredentialsDetailsServiceImplTest {

    @Mock
    private AuthCredentialsRepository repository;

    @InjectMocks
    private AuthCredentialsDetailsServiceImpl authCredentialsDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testLoadUserByUsername_UserFound() {
        // Mock data
        String username = "testuser";
        AuthCredentials authCredentials = new AuthCredentials();
        authCredentials.setUsername(username);
        authCredentials.setPassword("testpassword");

        when(repository.findByUsername(username)).thenReturn(Optional.of(authCredentials));

        // Call the method
        UserDetails userDetails = authCredentialsDetailsService.loadUserByUsername(username);

        // Verify the result
        assertEquals(username, userDetails.getUsername());
        verify(repository, times(1)).findByUsername(username);
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        // Mock data
        String username = "testuser";

        when(repository.findByUsername(username)).thenReturn(Optional.empty());

        // Call the method and verify it throws UsernameNotFoundException
        assertThrows(UsernameNotFoundException.class, () -> authCredentialsDetailsService.loadUserByUsername(username));
    }
}
