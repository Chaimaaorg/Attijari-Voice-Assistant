package com.attijari.bankingservices.testingservices;

import com.attijari.bankingservices.models.Agence;
import com.attijari.bankingservices.models.Client;
import com.attijari.bankingservices.models.Compte;
import com.attijari.bankingservices.models.Utilisateur;
import com.attijari.bankingservices.repositories.AgenceRepository;
import com.attijari.bankingservices.repositories.CompteRepository;
import com.attijari.bankingservices.repositories.UtilisateurRepository;
import com.attijari.bankingservices.services.implementations.AgenceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AgenceServiceImplTest {

    @Mock
    private AgenceRepository agenceRepository;
    @Mock
    private UtilisateurRepository utilisateurRepository;
    @Mock
    private CompteRepository compteRepository;

    @InjectMocks
    private AgenceServiceImpl agenceService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testGetAllAgencies() {
        // Mock data
        List<Agence> agences = new ArrayList<>();
        Agence agence1 = new Agence();
        agence1.setIdAgence(1L);
        agence1.setNomAgence("Agence 1");
        agences.add(agence1);

        when(agenceRepository.findAll()).thenReturn(agences);

        // Call the method
        List<Agence> result = agenceService.getAllAgencies();

        // Verify the result
        assertEquals(agences, result);
    }

    @Test
    void testGetAgenciesByUserId() {
        // Mock data
        long userId = 1L;
        Client client = new Client();
        client.setIdClient(userId);

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setClient(client);

        Compte compte = new Compte();
        compte.setAgence(new Agence());

        List<Compte> comptes = new ArrayList<>();
        comptes.add(compte);

        when(utilisateurRepository.findById(userId)).thenReturn(Optional.of(utilisateur));
        when(compteRepository.findByClientId(userId)).thenReturn(comptes);

        // Call the method
        List<Agence> result = agenceService.getAgenciesByUserId(userId);

        // Verify the result
        assertEquals(1, result.size());
    }

    @Test
    void testGetAgenciesByRegistredAddress() {
        // Mock data
        long userId = 1L;
        Utilisateur user = new Utilisateur();
        user.setIdUser(userId);
        Client client = new Client();
        client.setAdresse("58 allée des sophoras , Ain Sebaâ, Casablanca");
        user.setClient(client);

        when(utilisateurRepository.findById(userId)).thenReturn(Optional.of(user));

        // Call the method
        List<Agence> result = agenceService.getAgenciesByRegistredAddress(userId);

        // Verify the result
        assertEquals(0, result.size()); // Assuming empty list because no agencies found for the mocked address
    }

    @Test
    void testGetAgenciesByUserLocation() {
        // Mock data
        BigDecimal latitude = BigDecimal.valueOf(33.60912418010572);
        BigDecimal longitude = BigDecimal.valueOf(-7.541579746118355);

        Agence agence1 = new Agence();
        agence1.setAdresse("angle 7 km, Route de Rabat et Allée des Troènes Aïn Sebaâ, Aïn Sebaâ");

        Agence agence2 = new Agence();
        agence2.setAdresse("Paris"); // This address will be different

        // Mocking the repositories
        List<Agence> allAgencies = new ArrayList<>();
        allAgencies.add(agence1);
        allAgencies.add(agence2);
        when(agenceRepository.findAll()).thenReturn(allAgencies);

        // Call the method
        List<Agence> result = agenceService.getAgenciesByUserLocation(latitude, longitude);

        // Verify the result
        assertEquals(1, result.size()); // We expect only one agency to match the provided location
    }

}
