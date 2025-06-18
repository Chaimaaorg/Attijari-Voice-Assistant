package com.attijari.bankingservices.controllers;

import com.attijari.bankingservices.models.Beneficiaire;
import com.attijari.bankingservices.services.BeneficiaireService;
import com.attijari.bankingservices.utils.ManageUserBeneficiaires;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/beneficiaires")
@Tag(name = "Bénéficiare", description = "Gestion des bénéficiares des clients")
public class BeneficiaireController {

    @Autowired
    private BeneficiaireService beneficiaireService;

    @PostMapping("/add")
    @Operation(summary="Méthode pour ajouter un bénéficiaires")
    public ResponseEntity<String> addBeneficiary(@RequestBody ManageUserBeneficiaires manageUserBeneficiaires) {
        beneficiaireService.addBeneficiary(manageUserBeneficiaires.getUserId(), manageUserBeneficiaires.getBeneficiaire().getRib(), manageUserBeneficiaires.getBeneficiaire().getPrenom(), manageUserBeneficiaires.getBeneficiaire().getNom(), manageUserBeneficiaires.getBeneficiaire().getTypeBeneficiaire());
        return ResponseEntity.status(HttpStatus.CREATED).body("Votre bénéficiaire a été ajouté avec succès!");
    }

    @PostMapping("/user/names")
    @Operation(summary="Méthode pour récupérer les bénéficiaires par id d'utilisateur et nom du bénéficiaire")
    public ResponseEntity<List<Beneficiaire>> getBeneficiariesByName(@RequestBody ManageUserBeneficiaires manager) {
        List<Beneficiaire> beneficiaries = beneficiaireService.getBeneficiaryByUserIdNames(manager.getUserId(), manager.getBeneficiaire().getPrenom(), manager.getBeneficiaire().getNom());
        if (!beneficiaries.isEmpty()) {
            return ResponseEntity.ok(beneficiaries);
        }
        return ResponseEntity.notFound().build();
    }
    @PostMapping("/user/rib")
    @Operation(summary="Méthode pour récupérer les bénéficiaires par id d'utilisateur et rib du bénéficiaire")
    public ResponseEntity<Beneficiaire> getBeneficiaryByUserIdAndRib(@RequestBody ManageUserBeneficiaires manager) {
         Beneficiaire beneficiary = beneficiaireService.getBeneficiaryByUserIdAndRib(manager.getUserId(), manager.getBeneficiaire().getRib());
        if (beneficiary!=null) {
            return ResponseEntity.ok(beneficiary);
        }
        return ResponseEntity.notFound().build();
    }


    @PutMapping("/update/rib")
    @Operation(summary="Méthode pour modifier un bénéficiaire par id d'utilisateur et rib du bénéficiaire")
    public ResponseEntity<String> updateBeneficiaryByRib(@RequestBody ManageUserBeneficiaires manager) {
        Beneficiaire beneficiaire = beneficiaireService.getBeneficiaryByUserIdAndRib(manager.getUserId(), manager.getOldRib());
        if (beneficiaire != null) {
            beneficiaireService.updateBeneficiaryByRib(manager.getUserId(), manager.getOldRib(), manager.getNewRib());
            return ResponseEntity.ok("Le RIB du bénéficiaire a été mis à jour avec succès!");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Aucun bénéficiaire ne correspond à ce RIB.");
    }

    @PutMapping("/update/names")
    @Operation(summary="Méthode pour modifier un bénéficiaire par id d'utilisateur et nom du bénéficiaire")
    public ResponseEntity<String> updateBeneficiaryByNames(@RequestBody ManageUserBeneficiaires manager) {
        List<Beneficiaire> beneficiaires = beneficiaireService.getBeneficiaryByUserIdNames(manager.getUserId(), manager.getBeneficiaire().getPrenom(), manager.getBeneficiaire().getNom());
        if (beneficiaires.size() > 1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Vous avez plus qu'un bénéficiaire avec le même nom!");
        } else if (beneficiaires.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vous n'avez pas de bénéficiaires avec ces noms!");
        } else {
            beneficiaireService.updateBeneficiaryByNames(manager.getUserId(), manager.getBeneficiaire().getPrenom(), manager.getBeneficiaire().getNom(), manager.getNewRib());
            return ResponseEntity.ok("Votre bénéficiaire a été mis à jour avec succès!");
        }
    }

    @DeleteMapping("/delete/names")
    @Operation(summary="Méthode pour supprimer un bénéficiaire par id d'utilisateur et nom du bénéficiaire")
    public ResponseEntity<String> deleteBeneficiaryByNames(@RequestBody ManageUserBeneficiaires manager) {
        List<Beneficiaire> beneficiares = beneficiaireService.getBeneficiaryByUserIdNames(manager.getUserId(), manager.getBeneficiaire().getPrenom(), manager.getBeneficiaire().getNom());
        if (beneficiares.size() > 1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Vous avez plus qu'un bénéficiaire avec le même nom!");
        } else if (beneficiares.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vous n'avez pas de bénéficiaires avec ces noms!");
        } else {
            beneficiaireService.deleteBeneficiaryByNames(manager.getUserId(), manager.getBeneficiaire().getPrenom(), manager.getBeneficiaire().getNom());
            return ResponseEntity.ok("Votre bénéficiaire a été supprimé avec succès!");
        }
    }

    @DeleteMapping("/delete/rib")
    @Operation(summary="Méthode pour supprimer un bénéficiaire par id d'utilisateur et rib")
    public ResponseEntity<String> deleteBeneficiaryByRib(@RequestBody ManageUserBeneficiaires manager) {
        Beneficiaire beneficiaire = beneficiaireService.getBeneficiaryByUserIdAndRib(manager.getUserId(), manager.getBeneficiaire().getRib());
        if (beneficiaire != null) {
            beneficiaireService.deleteBeneficiaryByRib(manager.getUserId(), manager.getBeneficiaire().getRib());
            return ResponseEntity.ok("Votre bénéficiaire a été supprimé avec succès!");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Aucun bénéficiaire ne correspond à ce RIB.");
    }
}
