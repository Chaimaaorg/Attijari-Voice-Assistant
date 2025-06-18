package com.attijari.bankingservices.controllers;

import com.attijari.bankingservices.models.Carte;
import com.attijari.bankingservices.services.CarteService;
import com.attijari.bankingservices.utils.ManageDictEntities;
import com.attijari.bankingservices.utils.ManageUserCartes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cartes")
@Tag(name = "Carte", description = "Gestion des cartes de clients")
public class CarteController {

    @Autowired
    private CarteService carteService;

    @GetMapping("/user/{userId}")
    @Operation(summary = "Méthode pour récupérer les cartes par id d'utilisateur")
    public ResponseEntity<List<Carte>> getCardByUser(@PathVariable Long userId) {
        List<Carte> cartes = carteService.getCardByUserId(userId);
        if (cartes != null) {
            return ResponseEntity.ok(cartes);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/userCardsByType/{userId}/{typeCard}")
    @Operation(summary = "Méthode pour récupérer les cartes par id d'utilisateur et type de carte")
    public ResponseEntity<List<Carte>> getCardByType(@PathVariable Long userId, @PathVariable String typeCard) {
        List<Carte> cartes = carteService.getCardByUserIdAndType(userId, typeCard);
        if (cartes != null) {
            return ResponseEntity.ok(cartes);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/userCardsByNum/{numCard}")
    @Operation(summary = "Méthode pour récupérer les cartes par id d'utilisateur et type de carte")
    public ResponseEntity<Optional<Carte>> getCardByNum(@PathVariable Long cardNum) {
        Optional<Carte> cartes = carteService.getCardByNum(cardNum);
        if (cartes.isPresent()) {
            return ResponseEntity.ok(cartes);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/searchEntitiesDict")
    @Operation(summary = "Méthode pour récupérer les cartes par id d'utilisateur et autres attributs de carte")
    public ResponseEntity<List<Carte>> getCardByUserIdAndEntities(@RequestBody ManageDictEntities manager) {
        List<Carte> cartes = carteService.getCardByUserIdAndEntities(manager.getUserId(), manager.getEntitiesDic());
        if (!cartes.isEmpty()) {
            return ResponseEntity.ok(cartes);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/updateByCardNum")
    @Operation(summary = "Méthode pour modifier les services d'une carte par id d'utilisateur et numéro de carte")
    public ResponseEntity<String> updateCardServicesByCardNum(@RequestBody ManageUserCartes manager) {
        Long numeroCarte = manager.getCarte().getNumeroCarte();
        Optional<Carte> card = carteService.getCardByNum(numeroCarte);
        if (card.isPresent()) {
            if (!card.get().getStatutCarte().equals("opposée")) {
                carteService.updateCardServicesByCardNum(manager.getUserId(), manager.getCarte().getNumeroCarte(), manager.getServices(), manager.getStatus());
                return ResponseEntity.status(HttpStatus.CREATED).body("Les services de la carte sont mis à jour!");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Vous ne pouvez pas effectuer cette opération car cette carte a été opposée!");
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Aucune carte ne correspond à ce numéro!");
        }
    }

    @PutMapping("/updateByCardType")
    @Operation(summary = "Méthode pour modifier les services d'une carte par id d'utilisateur et type de carte")
    public ResponseEntity<String> updateCardServicesByCardType(@RequestBody ManageUserCartes manager) {
        List<Carte> cartes = carteService.getCardByUserIdAndType(manager.getUserId(), manager.getCarte().getTypeCarte());
        if (!cartes.isEmpty()) {
            if (cartes.size() == 1) {
                if (!cartes.get(0).getStatutCarte().equals("opposée")) {
                    carteService.updateCardServicesByCardType(manager.getUserId(), manager.getCarte().getTypeCarte(), manager.getServices(), manager.getStatus());
                    return ResponseEntity.status(HttpStatus.CREATED).body("Les services de la carte sont mis à jour!");
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Vous ne pouvez pas effectuer cette opération car cette carte a été opposée!");
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Vous avez plusieurs cartes de même type. Veuillez spécifier le numéro de la carte!");
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Aucune carte ne correspond à ce type!");
        }
    }

    @PutMapping("/opposeByCardNum")
    @Operation(summary = "Méthode pour opposer une carte par id d'utilisateur et numéro de carte")
    public ResponseEntity<String> opposeCardByCardNum(@RequestBody ManageUserCartes manager) {
        Long numeroCarte = manager.getCarte().getNumeroCarte();
        Optional<Carte> card = carteService.getCardByNum(numeroCarte);
        if (card.isPresent()) {
            Carte carte = card.get();
            if (carte.getStatutCarte().equals("opposée")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cette carte est déjà opposée!");
            } else {
                carteService.opposeCardByCardNum(manager.getUserId(), manager.getCarte().getNumeroCarte(), manager.getCarte().getRaisonsOpposition());
                return ResponseEntity.status(HttpStatus.CREATED).body("La carte est opposée avec succès!");
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Aucune carte ne correspond à ce numéro!");
        }
    }

    @PutMapping("/opposeByCardType")
    @Operation(summary = "Méthode pour opposer une carte par id d'utilisateur et type de carte")
    public ResponseEntity<String> opposeCardByCardType(@RequestBody ManageUserCartes manager) {
        List<Carte> cartes = carteService.getCardByUserIdAndType(manager.getUserId(), manager.getCarte().getTypeCarte());
        if (!cartes.isEmpty()) {
            if (cartes.size() == 1) {
                String status = cartes.get(0).getStatutCarte();
                if (status.equals("opposée")) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cette carte est déjà opposée!");
                }
                carteService.opposeCardByCardType(manager.getUserId(), manager.getCarte().getTypeCarte(), manager.getCarte().getRaisonsOpposition());
                return ResponseEntity.status(HttpStatus.CREATED).body("La carte est opposée avec succès!");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Veuillez spécifier le numéro de la carte car vous avez plusieurs cartes de même type!");
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Aucune carte ne correspond à ce type!");
        }
    }

}
