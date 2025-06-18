package com.attijari.bankingservices.testingservices;

import com.attijari.bankingservices.models.Beneficiaire;
import com.attijari.bankingservices.models.Client;
import com.attijari.bankingservices.models.Utilisateur;
import com.attijari.bankingservices.repositories.BeneficiaireRepository;
import com.attijari.bankingservices.repositories.UtilisateurRepository;
import com.attijari.bankingservices.services.implementations.BeneficiaireServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BeneficiaireServiceImplTest {

    @Mock
    private BeneficiaireRepository beneficiaireRepository;
    @Mock
    private UtilisateurRepository utilisateurRepository;

    @InjectMocks
    private BeneficiaireServiceImpl beneficiaireService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    // Méthode privée pour créer un utilisateur simulé
    private Utilisateur createMockUser(Long userId, Long clientId) {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setIdUser(userId);
        Client client = new Client();
        client.setIdClient(clientId);
        utilisateur.setClient(client);
        return utilisateur;
    }

    @Test
    void testAddBeneficiary_Success() {
        // Mock data
        Long userId = 1L;
        String rib = "123456789";
        String firstname = "John";
        String lastname = "Doe";
        String type = "TYPE";

        Utilisateur utilisateur = createMockUser(userId, 1L);
        when(utilisateurRepository.findById(userId)).thenReturn(Optional.of(utilisateur));

        // Call the method
        beneficiaireService.addBeneficiary(userId, rib, firstname, lastname, type);

        // Verify the result
        verify(beneficiaireRepository, times(1)).save(any());
    }

    @Test
    void testGetBeneficiaryByUserIdNames_Success() {
        // Mock data
        Long userId = 1L;
        String firstname = "John";
        String lastname = "Doe";
        List<Beneficiaire> beneficiaries = new ArrayList<>();
        beneficiaries.add(new Beneficiaire());

        Utilisateur utilisateur = createMockUser(userId, 1L);
        when(utilisateurRepository.findById(userId)).thenReturn(Optional.of(utilisateur));
        when(beneficiaireRepository.findByClientIdAndNames(utilisateur.getClient().getIdClient(), firstname, lastname)).thenReturn(beneficiaries);

        // Call the method
        List<Beneficiaire> result = beneficiaireService.getBeneficiaryByUserIdNames(userId, firstname, lastname);

        // Verify the result
        assertEquals(beneficiaries, result);
    }

    @Test
    void testDeleteBeneficiaryByRib_Success() {
        // Mock data
        Long userId = 1L;
        String rib = "123456789";

        Utilisateur utilisateur = createMockUser(userId, 1L);
        Beneficiaire beneficiaireToDelete = new Beneficiaire();
        when(utilisateurRepository.findById(userId)).thenReturn(Optional.of(utilisateur));
        when(beneficiaireRepository.findByClientIdAndRib(utilisateur.getClient().getIdClient(), rib)).thenReturn(beneficiaireToDelete);

        // When
        beneficiaireService.deleteBeneficiaryByRib(userId, rib);

        // Then
        // Verify that delete method is called with the expected argument
        verify(beneficiaireRepository).delete(any(Beneficiaire.class));
    }

    @Test
    void testGetBeneficiaryByUserIdAndRib_Success() {
        // Mock data
        Long userId = 1L;
        String rib = "123456789";

        Utilisateur utilisateur = createMockUser(userId, 1L);
        Beneficiaire beneficiary = new Beneficiaire(); // Create a new beneficiary to return
        when(utilisateurRepository.findById(userId)).thenReturn(Optional.of(utilisateur));
        when(beneficiaireRepository.findByClientIdAndRib(utilisateur.getClient().getIdClient(), rib)).thenReturn(beneficiary); // Return the beneficiary

        // Call the method
        Beneficiaire result = beneficiaireService.getBeneficiaryByUserIdAndRib(userId, rib);

        // Verify the result
        assertNotNull(result); // Assert that the result is not null
    }

}
