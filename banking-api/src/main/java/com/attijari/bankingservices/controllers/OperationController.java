package com.attijari.bankingservices.controllers;

import com.attijari.bankingservices.models.Beneficiaire;
import com.attijari.bankingservices.models.Compte;
import com.attijari.bankingservices.models.Operation;
import com.attijari.bankingservices.services.BeneficiaireService;
import com.attijari.bankingservices.services.CompteService;
import com.attijari.bankingservices.services.OperationService;
import com.attijari.bankingservices.utils.ManageDictEntities;
import com.attijari.bankingservices.utils.ManageUserOperations;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/operations")
@Tag(name = "Opérations", description = "Gestion des opérations des clients")
public class OperationController {

    private final OperationService OperationService;
    private final CompteService compteService;
    private final BeneficiaireService beneficiaireService;

    @Autowired
    public OperationController(com.attijari.bankingservices.services.OperationService operationService, CompteService compteService, BeneficiaireService beneficiaireService) {
        OperationService = operationService;
        this.compteService = compteService;
        this.beneficiaireService = beneficiaireService;
    }


    @GetMapping("/user/{userId}")
    @io.swagger.v3.oas.annotations.Operation(summary = "Méthode pour récupérer les transactions par utilisateur")
    public ResponseEntity<List<Operation>> getTransactionsByUserId(@PathVariable Long userId) {
        List<Operation> operations = OperationService.getTransactionByUserId(userId);
        if (operations != null) {
            return ResponseEntity.ok(operations);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/searchEntitiesDict")
    @io.swagger.v3.oas.annotations.Operation(summary = "Méthode pour récupérer les transactions d'un utilisateur donné avec des attributs spécifiques")
    public ResponseEntity<List<Operation>> getTransactionByUserIdEntities(@RequestBody ManageDictEntities manager) {
        List<Operation> operations = OperationService.getTransactionByUserIdEntities(manager.getUserId(), manager.getEntitiesDic());
        if (!operations.isEmpty()) {
            return ResponseEntity.ok(operations);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/addByAccountTypeAndRib")
    @io.swagger.v3.oas.annotations.Operation(summary = "Méthode pour ajouter une transaction par spécification du type de compte")
    public ResponseEntity<String> addTransactionByAccountTypeAndRib(@RequestBody ManageUserOperations m) {
        List<Compte> accounts = compteService.getAccountByUserIdAndAccountType(m.getUserId(), m.getCompte().getTypeCompte());
        if (!accounts.isEmpty()) {
            if (accounts.size() > 1) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Plusieurs comptes sont de même type. Veuillez spécifier le numéro de compte!");
            }
            else{
                if(accounts.get(0).getStatutCompte().equals("actif")){
                    BigDecimal balance = accounts.get(0).getSolde();
                    BigDecimal amount = m.getOperation().getMontant();
                    int comparisonResult = balance.compareTo(amount);
                    if (comparisonResult < 0) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Votre solde courant est insuffisant pour effectuer cette transaction!");
                    }
                    else{
                        OperationService.addTransactionByAccountType(m.getUserId(), m.getCompte().getTypeCompte(), m.getBeneficiaire().getRib(), m.getOperation().getMontant(), m.getOperation().getMotif(), m.getOperation().getTypeOperation());
                        return ResponseEntity.status(HttpStatus.CREATED).body("La transaction est passée avec succès!");
                    }
                }
                else{
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Votre compte a été fermé, vous ne pouvez pas effectuer cette transaction!");
                }
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Aucun compte ne correspond à ce type!");
        }

    }

    @PostMapping("/addByAccountTypeBeneficiaryNames")
    @io.swagger.v3.oas.annotations.Operation(summary = "Méthode pour ajouter une transaction par spécification des noms de bénéficiaire et le type de compte")
    public ResponseEntity<String> addTransactionByAccountTypeAndBenefeciaryNames(@RequestBody ManageUserOperations m) {
        List<Beneficiaire> beneficiaries = beneficiaireService.getBeneficiaryByUserIdNames(m.getUserId(), m.getBeneficiaire().getPrenom(), m.getBeneficiaire().getNom());
        if (!beneficiaries.isEmpty()) {
            if (beneficiaries.size() > 1) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Vous avez plusieurs bénéficiaires avec le même nom. Veuillez préciser le RIB du bénéficiaire!");
            }
            else{
                Beneficiaire beneficiaire = beneficiaries.get(0);
                m.setBeneficiaire(beneficiaire);
                return addTransactionByAccountTypeAndRib(m);
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Aucun bénéficiaire n'a pas été trouvé. Veuillez en ajouter un pour effectuer la transaction!");
        }
    }

    @PostMapping("/addByAccountNumAndRib")
    @io.swagger.v3.oas.annotations.Operation(summary = "Méthode pour ajouter une transaction par spécification du numéro de compte")
    public ResponseEntity<String> addTransactionByAccountNumAndRib(@RequestBody ManageUserOperations m) {
        Optional<Compte> account = compteService.getAccountByUserIdAndAccountNum(m.getUserId(), m.getCompte().getNumeroCompte());
        if (account.isPresent()) {
            if(!account.get().getStatutCompte().equals("actif")){
                BigDecimal balance = account.get().getSolde();
                BigDecimal amount = m.getOperation().getMontant();
                int comparisonResult = balance.compareTo(amount);
                if (comparisonResult < 0) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Votre solde courant est insuffisant pour effectuer cette transaction!");
                }
                else {
                    OperationService.addTransactionByAccountNum(m.getUserId(), m.getCompte().getNumeroCompte(), m.getBeneficiaire().getRib(),m.getOperation().getMontant(), m.getOperation().getMotif(), m.getOperation().getTypeOperation());
                    return ResponseEntity.status(HttpStatus.CREATED).body("La transaction est passée avec succès!");
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Votre compte a été fermé, vous ne pouvez pas effectuer cette transaction!");
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Aucun compte ne correspond à ce numéro!");
        }

    }
    @PostMapping("/addByAccountNumBeneficiaryNames")
    @io.swagger.v3.oas.annotations.Operation(summary = "Méthode pour ajouter une transaction par spécification des noms de bénéficiaire et le numéro de compte")
    public ResponseEntity<String> addTransactionByAccountNumAndBenefeciaryNames(@RequestBody ManageUserOperations m) {
        List<Beneficiaire> beneficiaries = beneficiaireService.getBeneficiaryByUserIdNames(m.getUserId(), m.getBeneficiaire().getPrenom(), m.getBeneficiaire().getNom());
        if (!beneficiaries.isEmpty()) {
            if (beneficiaries.size() > 1) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Veuillez préciser le RIB du bénéficiaire car vous avez plusieurs bénéficiaires avec le même nom!");
            }
            else{
                Beneficiaire beneficiaire = beneficiaries.get(0);
                m.setBeneficiaire(beneficiaire);
                return addTransactionByAccountNumAndRib(m);
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Aucun bénéficiaire ne correspond à ces noms. Veuillez en ajouter un pour effectuer la transaction!");
        }
    }

}

