package com.attijari.bankingservices.controllers;

import com.attijari.bankingservices.models.Compte;
import com.attijari.bankingservices.services.CompteService;
import com.attijari.bankingservices.utils.ManageDictEntities;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/comptes")
@Tag(name = "Compte",description = "Gestion des comptes des clients" )
public class CompteController {

    @Autowired
    private CompteService compteService;

    @GetMapping("/user/{userId}")
    @Operation(summary="Méthode pour récupérer les comptes par id d'utilisateur")
    public ResponseEntity<List<Compte>> getAccountByUserID(@PathVariable Long userId) {
        List<Compte> accounts = compteService.getAccountByUserID(userId);;
        if (!accounts.isEmpty()) {
            return ResponseEntity.ok(accounts);
        }
        return ResponseEntity.notFound().build();
    }
    @GetMapping("/userTypeCompte/{userId}/{typeCompte}")
    @Operation(summary="Méthode pour récupérer les comptes par id d'utilisateur et type de compte")
    public ResponseEntity<List<Compte>> getAccountByUserIdAccountType(@PathVariable Long userId,@PathVariable String typeCompte) {
        List<Compte> accounts = compteService.getAccountByUserIdAndAccountType(userId,typeCompte);
        if (!accounts.isEmpty()) {
            return ResponseEntity.ok(accounts);
        }
        return ResponseEntity.notFound().build();
    }
    @GetMapping("/userNumCompte/{userId}/{numeroCompte}")
    @Operation(summary="Méthode pour récupérer les comptes par id d'utilisateur et numéro de compte")
    public ResponseEntity<Optional<Compte>> getAccountByUserIdAndAccountNum(@PathVariable Long userId,@PathVariable String numeroCompte) {
        Optional<Compte> account = compteService.getAccountByUserIdAndAccountNum(userId,numeroCompte);
        if (account.isPresent()) {
            return ResponseEntity.ok(account);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/searchEntitiesDict")
    @Operation(summary="Méthode pour récupérer les comptes par id d'utilisateur et autres attributs de compte")
    public ResponseEntity<List<Compte>> getAccountByUserIdEntities(@RequestBody ManageDictEntities manager) {
        List<Compte> accounts = compteService.getAccountsByUserIdAndEntities(manager.getUserId(),manager.getEntitiesDic());
        if (!accounts.isEmpty()) {
            return ResponseEntity.ok(accounts);
        }
        return ResponseEntity.notFound().build();
    }
    @PostMapping("/balanceEntitiesDict")
    @Operation(summary="Méthode pour récupérer les soldes de comptes par id d'utilisateur")
    public ResponseEntity<Map<BigDecimal,Compte>> getBalanceByUserIdEntities(@RequestBody ManageDictEntities manager) {
        Map<BigDecimal,Compte> accounts = compteService.getBalanceByUserIdAndEntities(manager.getUserId(),manager.getEntitiesDic());
        if (!accounts.isEmpty()) {
            return ResponseEntity.ok(accounts);
        }
        return ResponseEntity.notFound().build();
    }
}

