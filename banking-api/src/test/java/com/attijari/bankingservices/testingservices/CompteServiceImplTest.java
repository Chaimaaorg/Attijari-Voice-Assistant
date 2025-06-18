package com.attijari.bankingservices.testingservices;

import com.attijari.bankingservices.models.Client;
import com.attijari.bankingservices.models.Compte;
import com.attijari.bankingservices.models.Utilisateur;
import com.attijari.bankingservices.repositories.CompteRepository;
import com.attijari.bankingservices.repositories.UtilisateurRepository;
import com.attijari.bankingservices.services.implementations.CompteServiceImpl;
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

class CompteServiceImplTest {

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private CompteRepository compteRepository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private CompteServiceImpl compteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testGetAccountByUserID() {
        // Mock data
        Long userId = 1L;
        Utilisateur user = new Utilisateur();
        user.setIdUser(userId);
        Client client = new Client();
        client.setIdClient(userId);
        user.setClient(client);
        List<Compte> expectedComptes = new ArrayList<>();
        Compte compte1 = new Compte();
        compte1.setNumeroCompte("123");
        compte1.setTypeCompte("Checking");
        compte1.setClient(client);
        Compte compte2 = new Compte();
        compte2.setNumeroCompte("456");
        compte2.setTypeCompte("Savings");
        compte2.setClient(client);
        expectedComptes.add(compte1);
        expectedComptes.add(compte2);

        when(utilisateurRepository.findById(userId)).thenReturn(Optional.of(user));
        when(compteRepository.findByClientId(user.getClient().getIdClient())).thenReturn(expectedComptes);

        // Call the method
        List<Compte> result = compteService.getAccountByUserID(userId);

        // Verify the result
        assertEquals(expectedComptes, result);
    }

    @Test
    void testGetAccountByUserIdAndAccountType() {
        // Mock data
        Long userId = 1L;
        Utilisateur user = new Utilisateur();
        user.setIdUser(userId);
        Client client = new Client();
        client.setIdClient(userId);
        user.setClient(client);
        String accountType = "Checking";
        Compte compte1 = new Compte();
        compte1.setNumeroCompte("123");
        compte1.setTypeCompte("Checking");
        compte1.setClient(client);
        Compte compte2 = new Compte();
        compte2.setNumeroCompte("456");
        compte2.setTypeCompte("Savings");
        compte2.setClient(client);
        List<Compte> comptes = Arrays.asList(compte1, compte2);
        List<Compte> expectedComptes = Collections.singletonList(compte1);

        // Mock the behavior of utilisateurRepository
        when(utilisateurRepository.findById(userId)).thenReturn(Optional.of(user));

        // Mock the behavior of compteRepository
        when(compteRepository.findByClientId(userId)).thenReturn(comptes);

        // Call the method
        List<Compte> result = compteService.getAccountByUserIdAndAccountType(userId, accountType);

        // Verify the result
        assertEquals(expectedComptes, result);
    }


    @Test
    void testGetAccountByUserIdAndAccountNum() {
        // Mock data
        Long userId = 1L;
        Utilisateur user = new Utilisateur();
        user.setIdUser(userId);
        Client client = new Client();
        client.setIdClient(userId);
        user.setClient(client);
        Long userId2 = 1L;
        Utilisateur user2 = new Utilisateur();
        user2.setIdUser(userId);
        Client client2 = new Client();
        client2.setIdClient(userId2);
        user2.setClient(client2);
        String accountNum = "123";
        Compte compte1 = new Compte();
        compte1.setNumeroCompte("123");
        compte1.setTypeCompte("Checking");
        compte1.setClient(client);
        Compte compte2 = new Compte();
        compte2.setNumeroCompte("456");
        compte2.setTypeCompte("Savings");
        compte2.setClient(client2);
        List<Compte> comptes = Arrays.asList(compte1, compte2);
        Optional<Compte> expectedCompte = Optional.of(compte1);

        // Mock the behavior of utilisateurRepository
        when(utilisateurRepository.findById(userId)).thenReturn(Optional.of(user));

        // Mock the behavior of compteRepository
        when(compteRepository.findByClientId(userId)).thenReturn(comptes);
        // Call the method
        Optional<Compte> result = compteService.getAccountByUserIdAndAccountNum(userId, accountNum);

        // Verify the result
        assertEquals(expectedCompte, result);
    }

    @Test
    void testGetAccountsByUserIdAndEntities() {
        // Mock data
        Long userId = 1L;
        Utilisateur user = new Utilisateur();
        user.setIdUser(userId);
        Client client = new Client();
        client.setIdClient(userId);
        user.setClient(client);
        Map<String, Object> entitiesDict = new HashMap<>();
        entitiesDict.put("type", "Checking");
        entitiesDict.put("balance", new BigDecimal("1000.00"));
        List<Compte> expectedComptes = new ArrayList<>();
        Compte compte1 = new Compte();
        compte1.setNumeroCompte("123");
        compte1.setTypeCompte("Checking");
        compte1.setSolde(new BigDecimal("1000.00"));
        compte1.setClient(client);
        expectedComptes.add(compte1);

        when(utilisateurRepository.findById(userId)).thenReturn(Optional.of(user));
        when(compteRepository.findByClientIdAndEntities(userId, entitiesDict, entityManager)).thenReturn(expectedComptes);

        // Call the method
        List<Compte> result = compteService.getAccountsByUserIdAndEntities(userId, entitiesDict);

        // Verify the result
        assertEquals(expectedComptes, result);
    }

    @Test
    void testGetBalanceByUserIdAndEntities() {
        // Mock data
        Long userId = 1L;
        Utilisateur user = new Utilisateur();
        user.setIdUser(userId);
        Client client = new Client();
        client.setIdClient(userId);
        user.setClient(client);
        Map<String, Object> entitiesDict = new HashMap<>();
        entitiesDict.put("type", "Checking");
        entitiesDict.put("balance", new BigDecimal("1000.00"));
        List<Compte> comptes = new ArrayList<>();
        Compte compte1 = new Compte();
        compte1.setNumeroCompte("123");
        compte1.setTypeCompte("Checking");
        compte1.setSolde(new BigDecimal("1000.00"));
        compte1.setClient(client);
        comptes.add(compte1);
        Map<BigDecimal, Compte> expectedBalance = new HashMap<>();
        expectedBalance.put(new BigDecimal("1000.00"), compte1);

        when(utilisateurRepository.findById(userId)).thenReturn(Optional.of(user));
        when(compteRepository.findByClientIdAndEntities(userId, entitiesDict, entityManager)).thenReturn(comptes);

        // Call the method
        Map<BigDecimal, Compte> result = compteService.getBalanceByUserIdAndEntities(userId, entitiesDict);

        // Verify the result
        assertEquals(expectedBalance, result);
    }

    @Test
    void testGetAccountUserId() {
        // Mock data
        Client client = new Client();
        client.setIdClient(2L);
        Client client2 = new Client();
        client2.setIdClient(5L);
        Utilisateur user1 = new Utilisateur();
        user1.setIdUser(1L);
        user1.setClient(client);
        Utilisateur user2 = new Utilisateur();
        user2.setIdUser(2L);
        user2.setClient(client2);
        Long idCompte = 1L;
        Compte compte = new Compte();
        compte.setIdCompte(idCompte);
        compte.setClient(client);
        List<Utilisateur> users = Arrays.asList(user1, user2);

        when(utilisateurRepository.findAll()).thenReturn(users);

        when(compteRepository.findById(idCompte)).thenReturn(Optional.of(compte));
        // Call the method
        Long result = compteService.getAccountUserId(idCompte);

        // Verify the result
        assertEquals(user1.getIdUser(), result);
    }
}
