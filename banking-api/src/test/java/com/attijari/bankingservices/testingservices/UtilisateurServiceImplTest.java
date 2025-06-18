package com.attijari.bankingservices.testingservices;

import com.attijari.bankingservices.models.Utilisateur;
import com.attijari.bankingservices.repositories.UtilisateurRepository;
import com.attijari.bankingservices.services.implementations.UtilisateurServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UtilisateurServiceImplTest {

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @InjectMocks
    private UtilisateurServiceImpl utilisateurService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testAuthenticateUser_ValidCredentials() {
        // Mock data
        String username = "user1";
        String password = "password123";
        Utilisateur user = new Utilisateur();
        user.setIdUser(1L);
        user.setUsername(username);
        user.setPassword(password);

        when(utilisateurRepository.findByUsername(username)).thenReturn(user);

        // Call the method
        Utilisateur authenticatedUser = utilisateurService.authenticateUser(username, password);

        // Verify
        assertNotNull(authenticatedUser);
        assertEquals(user.getIdUser(), authenticatedUser.getIdUser());
        assertEquals(user.getUsername(), authenticatedUser.getUsername());
        assertEquals(user.getPassword(), authenticatedUser.getPassword());
    }

    @Test
    void testAuthenticateUser_InvalidCredentials() {
        // Mock data
        String username = "user2";
        String password = "invalidpassword";

        when(utilisateurRepository.findByUsername(username)).thenReturn(null);

        // Call the method
        Utilisateur authenticatedUser = utilisateurService.authenticateUser(username, password);

        // Verify
        assertNull(authenticatedUser);
    }

    @Test
    void testAuthenticateUser_WrongPassword() {
        // Mock data
        String username = "user3";
        String correctPassword = "correctpassword";
        String wrongPassword = "wrongpassword";
        Utilisateur user = new Utilisateur();
        user.setUsername(username);
        user.setPassword(correctPassword);

        when(utilisateurRepository.findByUsername(username)).thenReturn(user);

        // Call the method
        Utilisateur authenticatedUser = utilisateurService.authenticateUser(username, wrongPassword);

        // Verify
        assertNull(authenticatedUser);
    }

    @Test
    void testGetUserById_UserExists() {
        // Mock data
        Long userId = 1L;
        Utilisateur user = new Utilisateur();
        user.setIdUser(userId);

        when(utilisateurRepository.findById(userId)).thenReturn(java.util.Optional.of(user));

        // Call the method
        Utilisateur foundUser = utilisateurService.getUserById(userId);

        // Verify
        assertNotNull(foundUser);
        assertEquals(userId, foundUser.getIdUser());
    }

    @Test
    void testGetUserById_UserDoesNotExist() {
        // Mock data
        Long userId = 2L;

        when(utilisateurRepository.findById(userId)).thenReturn(java.util.Optional.empty());

        // Call the method
        Utilisateur foundUser = utilisateurService.getUserById(userId);

        // Verify
        assertNull(foundUser);
    }
}
