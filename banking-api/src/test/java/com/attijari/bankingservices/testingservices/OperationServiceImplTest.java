package com.attijari.bankingservices.testingservices;

import com.attijari.bankingservices.models.*;
import com.attijari.bankingservices.repositories.BeneficiaireRepository;
import com.attijari.bankingservices.repositories.CompteRepository;
import com.attijari.bankingservices.repositories.OperationRepository;
import com.attijari.bankingservices.repositories.UtilisateurRepository;
import com.attijari.bankingservices.services.implementations.OperationServiceImpl;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class OperationServiceImplTest {

    @Mock
    UtilisateurRepository utilisateurRepository;

    @Mock
    CompteRepository compteRepository;

    @Mock
    OperationRepository operationRepository;

    @Mock
    BeneficiaireRepository beneficiaireRepository;

    @Mock
    EntityManager entityManager;

    @InjectMocks
    OperationServiceImpl operationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetTransactionByUserId() {
        // Mock data
        Long userId = 1L;
        Utilisateur user = new Utilisateur();
        user.setIdUser(userId);
        Client client = new Client();
        client.setIdClient(userId);
        user.setClient(client);
        Compte compte = new Compte();
        compte.setIdCompte(1L);
        List<Compte> accounts = Collections.singletonList(compte);
        when(utilisateurRepository.findById(userId)).thenReturn(Optional.of(user));
        when(compteRepository.findByClientId(userId)).thenReturn(accounts);
        Operation operation = new Operation();
        when(operationRepository.findByCompte(compte)).thenReturn(Collections.singletonList(operation));

        // Call the method
        List<Operation> result = operationService.getTransactionByUserId(userId);

        // Verify the result
        assertEquals(1, result.size());
    }

    @Test
    void testGetTransactionByUserIdEntities() {
        // Mock data
        Long userId = 1L;
        Utilisateur user = new Utilisateur();
        user.setIdUser(userId);
        Map<String, Object> entitiesDict = new HashMap<>();
        entitiesDict.put("type", "Deposit");
        entitiesDict.put("amount", new BigDecimal("100"));
        Client client = new Client();
        client.setIdClient(userId);
        user.setClient(client);
        when(utilisateurRepository.findById(userId)).thenReturn(Optional.of(user));
        when(operationRepository.findByClientIdAndEntities(userId, entitiesDict, entityManager)).thenReturn(new ArrayList<>());

        // Call the method
        List<Operation> result = operationService.getTransactionByUserIdEntities(userId, entitiesDict);

        // Verify the result
        assertEquals(0, result.size());
    }

    @Test
    void testAddTransactionByAccountType() {
        // Mock data
        Long userId = 1L;
        String accountType = "Checking";
        String ribBeneficiare = "123456";
        BigDecimal amount = new BigDecimal("100");
        String note = "Test";
        String type = "Deposit";
        Utilisateur user = new Utilisateur();
        user.setIdUser(userId);
        Client client = new Client();
        client.setIdClient(userId);
        user.setClient(client);
        Compte compte = new Compte();
        compte.setSolde(new BigDecimal("500"));
        compte.setIdCompte(1L);
        compte.setTypeCompte(accountType);
        Beneficiaire beneficiaire = new Beneficiaire();
        beneficiaire.setIdBeneficiaire(1L);
        List<Compte> accounts = Collections.singletonList(compte);
        when(utilisateurRepository.findById(userId)).thenReturn(Optional.of(user));
        when(compteRepository.findByClientId(userId)).thenReturn(accounts);
        when(beneficiaireRepository.findByClientIdAndRib(userId, ribBeneficiare)).thenReturn(beneficiaire);

        // Call the method
        operationService.addTransactionByAccountType(userId, accountType, ribBeneficiare, amount, note, type);

        // Verify
        verify(compteRepository, times(1)).save(compte);
        verify(operationRepository, times(1)).save(any(Operation.class));
    }

    @Test
    void testAddTransactionByAccountNum() {
        // Mock data
        Long userId = 1L;
        String accountNum = "123";
        String ribBeneficiare = "123456";
        BigDecimal amount = new BigDecimal("100");
        String note = "Test";
        String type = "Deposit";
        Utilisateur user = new Utilisateur();
        user.setIdUser(userId);
        Client client = new Client();
        client.setIdClient(userId);
        user.setClient(client);
        Compte compte = new Compte();
        compte.setIdCompte(1L);
        compte.setNumeroCompte(accountNum);
        compte.setSolde(new BigDecimal("500"));
        Beneficiaire beneficiaire = new Beneficiaire();
        beneficiaire.setIdBeneficiaire(1L);
        List<Compte> accounts = Collections.singletonList(compte);
        when(utilisateurRepository.findById(userId)).thenReturn(Optional.of(user));
        when(compteRepository.findByClientId(userId)).thenReturn(accounts);
        when(beneficiaireRepository.findByClientIdAndRib(userId, ribBeneficiare)).thenReturn(beneficiaire);

        // Call the method
        operationService.addTransactionByAccountNum(userId, accountNum, ribBeneficiare, amount, note, type);

        // Verify
        verify(compteRepository, times(1)).save(compte);
        verify(operationRepository, times(1)).save(any(Operation.class));
    }
}
