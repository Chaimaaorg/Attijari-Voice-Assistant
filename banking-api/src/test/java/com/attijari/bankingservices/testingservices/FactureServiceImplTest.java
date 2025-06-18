package com.attijari.bankingservices.testingservices;

import com.attijari.bankingservices.models.Beneficiaire;
import com.attijari.bankingservices.models.Client;
import com.attijari.bankingservices.models.Facture;
import com.attijari.bankingservices.models.Utilisateur;
import com.attijari.bankingservices.repositories.FactureRepository;
import com.attijari.bankingservices.repositories.UtilisateurRepository;
import com.attijari.bankingservices.services.implementations.FactureServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FactureServiceImplTest {

    @Mock
    private FactureRepository factureRepository;

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @InjectMocks
    private FactureServiceImpl factureService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testUpdateInvoiceStatus() {
        // Arrange
        Long userId = 1L;
        Long beneficiaryId = 2L;
        String status = "payée";
        Utilisateur utilisateur = new Utilisateur();
        Client client = new Client();
        client.setIdClient(1L);
        utilisateur.setClient(client);
        Beneficiaire beneficiaire = new Beneficiaire();
        beneficiaire.setIdBeneficiaire(beneficiaryId);
        Facture facture = new Facture();
        facture.setClient(client);
        facture.setBeneficiaire(beneficiaire);
        List<Facture> invoiceList = new ArrayList<>();
        invoiceList.add(facture);

        when(utilisateurRepository.findById(userId)).thenReturn(Optional.of(utilisateur));
        when(factureRepository.findAll()).thenReturn(invoiceList);

        // Act
        factureService.updateInvoiceStatus(userId, beneficiaryId, status);

        // Assert
        assertEquals(status, facture.getStatutPaiement());
        verify(factureRepository, times(1)).save(any(Facture.class));
    }

    @Test
    void testCheckInvoiceStatus() {
        // Arrange
        String numFacture = "123456";
        Facture facture = new Facture();
        facture.setStatutPaiement("payée");

        when(factureRepository.findByNumeroFacture(numFacture)).thenReturn(Optional.of(facture));

        // Act
        boolean result = factureService.checkInvoiceStatus(numFacture);

        // Assert
        assertTrue(result);
    }

    @Test
    void testCheckInvoiceStatus_NotFound() {
        // Arrange
        String numFacture = "123456";

        when(factureRepository.findByNumeroFacture(numFacture)).thenReturn(Optional.empty());

        // Act
        boolean result = factureService.checkInvoiceStatus(numFacture);

        // Assert
        assertFalse(result);
    }

    @Test
    void testGetInvoiceByNumber() {
        // Arrange
        String numFacture = "123456";
        Facture facture = new Facture();

        when(factureRepository.findByNumeroFacture(numFacture)).thenReturn(Optional.of(facture));

        // Act
        Optional<Facture> result = factureService.getInvoiceByNumber(numFacture);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(facture, result.get());
    }

    @Test
    void testGetInvoiceByNumber_NotFound() {
        // Arrange
        String numFacture = "123456";

        when(factureRepository.findByNumeroFacture(numFacture)).thenReturn(Optional.empty());

        // Act
        Optional<Facture> result = factureService.getInvoiceByNumber(numFacture);

        // Assert
        assertFalse(result.isPresent());
    }
}
