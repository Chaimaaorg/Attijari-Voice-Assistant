package com.attijari.bankingservices.services.implementations;

import com.attijari.bankingservices.models.Client;
import com.attijari.bankingservices.models.Compte;
import com.attijari.bankingservices.models.Utilisateur;
import com.attijari.bankingservices.repositories.CompteRepository;
import com.attijari.bankingservices.repositories.UtilisateurRepository;
import com.attijari.bankingservices.services.CompteService;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CompteServiceImpl implements CompteService {

    private final UtilisateurRepository utilisateurRepository;
    private final CompteRepository compteRepository;
    private final EntityManager entityManager;

    public CompteServiceImpl(UtilisateurRepository utilisateurRepository, CompteRepository compteRepository, EntityManager entityManager) {
        this.utilisateurRepository = utilisateurRepository;
        this.compteRepository = compteRepository;
        this.entityManager = entityManager;
    }

    @Override
    public List<Compte> getAccountByUserID(Long userId) {
        Client client = utilisateurRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found")).getClient();

        return compteRepository.findByClientId(client.getIdClient());
    }

    @Override
    public List<Compte> getAccountByUserIdAndAccountType(Long userID, String accountType) {
        return getAccountByUserID(userID).stream()
                .filter(compte -> compte.getTypeCompte().equals(accountType))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Compte> getAccountByUserIdAndAccountNum(Long userID, String accountNum) {
        return getAccountByUserID(userID).stream()
                .filter(compte -> compte.getNumeroCompte().equals(accountNum))
                .findFirst();
    }

    @Override
    public List<Compte> getAccountsByUserIdAndEntities(Long userId, Map<String, Object> entitiesDict) {
        Client client = utilisateurRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found")).getClient();
        Long clientId = client.getIdClient();

        return compteRepository.findByClientIdAndEntities(clientId, entitiesDict, entityManager);
    }

    @Override
    public Map<BigDecimal, Compte> getBalanceByUserIdAndEntities(Long userId, Map<String, Object> entitiesDict) {
        List<Compte> accounts = getAccountsByUserIdAndEntities(userId, entitiesDict);
        return accounts.stream().collect(Collectors.toMap(Compte::getSolde, account -> account));
    }

    @Override
    public Long getAccountUserId(Long idCompte) {
        Compte compte = compteRepository.findById(idCompte).orElse(null);
        if (compte != null) {
            Long idClient = compte.getClient().getIdClient();
            List<Utilisateur> users = utilisateurRepository.findAll();
            Long returnId = null;
            for (Utilisateur user : users) {
                if (user.getClient().getIdClient().equals(idClient)) {
                    returnId = user.getIdUser();
                }
            }
            return returnId;
        }
        return null;
    }
}

