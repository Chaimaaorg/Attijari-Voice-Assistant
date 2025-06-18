package com.attijari.bankingservices.services.implementations;

import com.attijari.bankingservices.models.*;
import com.attijari.bankingservices.repositories.BeneficiaireRepository;
import com.attijari.bankingservices.repositories.CompteRepository;
import com.attijari.bankingservices.repositories.OperationRepository;
import com.attijari.bankingservices.repositories.UtilisateurRepository;
import com.attijari.bankingservices.services.OperationService;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class OperationServiceImpl implements OperationService {

    private final UtilisateurRepository utilisateurRepository;
    private final CompteRepository compteRepository;
    private final OperationRepository operationRepository;
    private final BeneficiaireRepository beneficiaireRepository;
    private final EntityManager entityManager;


    public OperationServiceImpl(UtilisateurRepository utilisateurRepository, CompteRepository compteRepository, OperationRepository operationRepository, BeneficiaireRepository beneficiaireRepository, EntityManager entityManager) {
        this.utilisateurRepository = utilisateurRepository;
        this.compteRepository = compteRepository;
        this.operationRepository = operationRepository;
        this.beneficiaireRepository = beneficiaireRepository;
        this.entityManager = entityManager;
    }

    @Override
    public List<Operation> getTransactionByUserId(Long userId) {
        Optional<Utilisateur> user = utilisateurRepository.findById(userId);
        List<Operation> operations = new ArrayList<>();
        if (user.isPresent()) {
            Long clientId = user.get().getClient().getIdClient();
            List<Compte> comptes = compteRepository.findByClientId(clientId);
            for (Compte compte : comptes) {
                List<Operation> operationsForCompte = operationRepository.findByCompte(compte);
                operations.addAll(operationsForCompte);
            }
        }
        return operations;
    }

    @Override
    public List<Operation> getTransactionByUserIdEntities(Long userId, Map<String, Object> entitiesDict) {
        Client client = utilisateurRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found")).getClient();
        Long clientId = client.getIdClient();

        return operationRepository.findByClientIdAndEntities(clientId, entitiesDict, entityManager);
    }

    @Override
    public void addTransactionByAccountType(Long userId, String accountType, String ribBeneficiare, BigDecimal amount, String note, String type) {
        Optional<Utilisateur> user = utilisateurRepository.findById(userId);
        Operation operation = new Operation();
        if (user.isPresent()) {
            Long clientId = user.get().getClient().getIdClient();
            List<Compte> accounts = compteRepository.findByClientId(clientId);
            Beneficiaire beneficiaire = beneficiaireRepository.findByClientIdAndRib(clientId, ribBeneficiare);
            Compte account = accounts.get(0);
            if (account.getTypeCompte().equals(accountType)) {
                BigDecimal balance = account.getSolde();
                BigDecimal newBalance = balance.subtract(amount);
                account.setSolde(newBalance);
                compteRepository.save(account);
                operation.setTypeOperation(type);
                operation.setCompte(accounts.get(0));
                operation.setDateOperation(new Timestamp(System.currentTimeMillis()));
                operation.setMontant(amount);
                operation.setBeneficiaire(beneficiaire);
                operation.setMotif(note.isEmpty() ? "Sans motif" : note);
                operation.setCategorieOperation("Virement");
                operationRepository.save(operation);
            }
        }

    }

    @Override
    public void addTransactionByAccountNum(Long userId, String accountNum, String ribBeneficiare, BigDecimal amount, String note, String type) {
        Optional<Utilisateur> user = utilisateurRepository.findById(userId);
        Operation operation = new Operation();
        if (user.isPresent()) {
            Long clientId = user.get().getClient().getIdClient();
            List<Compte> accounts = compteRepository.findByClientId(clientId);
            Beneficiaire beneficiaire = beneficiaireRepository.findByClientIdAndRib(clientId, ribBeneficiare);
            for (Compte account : accounts) {
                if (account.getNumeroCompte().equals(accountNum)) {
                    BigDecimal balance = account.getSolde();
                    BigDecimal newBalance = balance.subtract(amount);
                    account.setSolde(newBalance);
                    compteRepository.save(account);
                    operation.setTypeOperation(type);
                    operation.setCompte(accounts.get(0));
                    operation.setDateOperation(new Timestamp(System.currentTimeMillis()));
                    operation.setMontant(amount);
                    operation.setBeneficiaire(beneficiaire);
                    operation.setMotif(note.isEmpty() ? "Sans motif" : note);
                    operation.setCategorieOperation("Virement");
                    operationRepository.save(operation);
                }

            }
        }
    }
}
