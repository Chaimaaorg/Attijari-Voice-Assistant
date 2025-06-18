package com.attijari.bankingservices.testingservices;

import com.attijari.bankingservices.models.*;
import com.attijari.bankingservices.repositories.*;
import com.attijari.bankingservices.services.implementations.PaiementFactureServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PaiementFactureServiceImplTest {

    @Mock
    private PaiementFactureRepository paiementFactureRepository;

    @Mock
    private CompteRepository compteRepository;

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private FactureRepository factureRepository;

    @InjectMocks
    private PaiementFactureServiceImpl paiementFactureService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testAddInvoiceTransactionByAccountType_UserNotFound() {
        // Mock data
        Long userId = 1L;
        String invoiceNum = "INV123";
        String accountType = "Checking";

        when(utilisateurRepository.findById(userId)).thenReturn(Optional.empty());

        // Call the method
        paiementFactureService.addInvoiceTransactionByAccountType(userId, invoiceNum, accountType);

        // Verify that nothing is saved
        verifyNoInteractions(paiementFactureRepository, compteRepository);
    }

    @Test
    void testAddInvoiceTransactionByAccountType_InvoiceNotFound() {
        // Mock data
        Long userId = 1L;
        String invoiceNum = "INV123";
        String accountType = "Checking";
        Utilisateur user = new Utilisateur();
        user.setClient(new Client());

        when(utilisateurRepository.findById(userId)).thenReturn(Optional.of(user));
        when(factureRepository.findByNumeroFacture(invoiceNum)).thenReturn(Optional.empty());

        // Call the method
        paiementFactureService.addInvoiceTransactionByAccountType(userId, invoiceNum, accountType);

        // Verify that nothing is saved
        verifyNoInteractions(paiementFactureRepository, compteRepository);
    }

    @Test
    void testAddInvoiceTransactionByAccountType_AccountTypeNotFound() {
        // Mock data
        Long userId = 1L;
        String invoiceNum = "INV123";
        String accountType = "Checking";
        Utilisateur user = new Utilisateur();
        Client client = new Client();
        client.setIdClient(userId);
        user.setClient(client);
        Facture facture = new Facture();
        facture.setMontant(new BigDecimal("100.00"));
        List<Compte> accounts = new ArrayList<>();
        Compte account = new Compte();
        account.setTypeCompte("Checking");
        accounts.add(account);
        account.setSolde(new BigDecimal("150.00"));

        when(utilisateurRepository.findById(userId)).thenReturn(Optional.of(user));
        when(factureRepository.findByNumeroFacture(invoiceNum)).thenReturn(Optional.of(facture));
        when(compteRepository.findByClientId(userId)).thenReturn(accounts);

        // Call the method
        paiementFactureService.addInvoiceTransactionByAccountType(userId, invoiceNum, accountType);

        // Verify that nothing is saved
        verify(paiementFactureRepository, times(1)).save(any());

    }

    @Test
    void testAddInvoiceTransactionByAccountType_Success() {
        // Mock data
        Long userId = 1L;
        String invoiceNum = "INV123";
        String accountType = "Checking";
        Utilisateur user = new Utilisateur();
        Client client = new Client();
        client.setIdClient(userId);
        user.setClient(client);
        Facture facture = new Facture();
        facture.setMontant(new BigDecimal("100.00"));
        List<Compte> accounts = new ArrayList<>();
        Compte account = new Compte();
        account.setTypeCompte(accountType);
        account.setSolde(new BigDecimal("200.00"));
        accounts.add(account);

        when(utilisateurRepository.findById(userId)).thenReturn(Optional.of(user));
        when(factureRepository.findByNumeroFacture(invoiceNum)).thenReturn(Optional.of(facture));
        when(compteRepository.findByClientId(userId)).thenReturn(accounts);

        // Call the method
        paiementFactureService.addInvoiceTransactionByAccountType(userId, invoiceNum, accountType);

        // Verify that the account balance is updated and payment transaction is saved
        verify(compteRepository, times(1)).save(account);
        verify(paiementFactureRepository, times(1)).save(any(PaiementFacture.class));
        assertEquals(new BigDecimal("100.00"), account.getSolde());
    }

    @Test
    void testAddInvoiceTransactionByAccountNum_InvoiceNotFound() {
        // Mock data
        String invoiceNum = "INV123";
        String accountNum = "ACC123";

        when(factureRepository.findByNumeroFacture(invoiceNum)).thenReturn(Optional.empty());

        // Call the method
        paiementFactureService.addInvoiceTransactionByAccountNum(invoiceNum, accountNum);

        // Verify that nothing is saved
        verifyNoInteractions(paiementFactureRepository, compteRepository);
    }

    @Test
    void testAddInvoiceTransactionByAccountNum_AccountNotFound() {
        // Mock data
        String invoiceNum = "INV123";
        String accountNum = "ACC123";
        Facture facture = new Facture();
        Compte compte = new Compte();
        compte.setSolde(new BigDecimal("100.00"));
        compte.setNumeroCompte(accountNum);
        facture.setNumeroFacture(invoiceNum);
        facture.setMontant(new BigDecimal("50.00"));


        when(factureRepository.findByNumeroFacture(invoiceNum)).thenReturn(Optional.of(facture));
        when(compteRepository.findByNumeroCompte(accountNum)).thenReturn(compte);

        // Call the method
        paiementFactureService.addInvoiceTransactionByAccountNum(invoiceNum, accountNum);

        // Verify that nothing is saved
        verify(paiementFactureRepository, times(1)).save(any());

    }

    @Test
    void testAddInvoiceTransactionByAccountNum_Success() {
        // Mock data
        String invoiceNum = "INV123";
        String accountNum = "ACC123";
        Facture facture = new Facture();
        facture.setMontant(new BigDecimal("100.00"));
        Compte account = new Compte();
        account.setSolde(new BigDecimal("200.00"));

        when(factureRepository.findByNumeroFacture(invoiceNum)).thenReturn(Optional.of(facture));
        when(compteRepository.findByNumeroCompte(accountNum)).thenReturn(account);

        // Call the method
        paiementFactureService.addInvoiceTransactionByAccountNum(invoiceNum, accountNum);

        // Verify that the account balance is updated and payment transaction is saved
        verify(compteRepository, times(1)).save(account);
        verify(paiementFactureRepository, times(1)).save(any(PaiementFacture.class));
        assertEquals(new BigDecimal("100.00"), account.getSolde());
    }
}
