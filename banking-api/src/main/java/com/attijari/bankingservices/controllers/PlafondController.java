package com.attijari.bankingservices.controllers;


import com.attijari.bankingservices.models.Carte;
import com.attijari.bankingservices.services.CarteService;
import com.attijari.bankingservices.services.PlafondService;
import com.attijari.bankingservices.utils.ManageUserCardLimits;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/plafond")
@Tag(name = "Plafond des cartes bancaires",description = "Gestion des plafonds des cartes bancaires des clients" )
public class PlafondController {


    private final PlafondService plafondService;
    private final CarteService carteService;

    @Autowired
    public PlafondController(PlafondService plafondService, CarteService carteService) {
        this.plafondService = plafondService;
        this.carteService = carteService;
    }

    @PutMapping("/updateByCardType")
    @Operation(summary = "Méthode pour modifier le plafond d'une carte d'un utilisateur par type de carte")
    public ResponseEntity<String> updateCardLimitByCardType(@RequestBody ManageUserCardLimits m) {
        List<Carte> cartes = carteService.getCardByUserIdAndType(m.getUserId(), m.getCarte().getTypeCarte());
        if (!cartes.isEmpty()) {
            if (cartes.size() > 1) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Vous disposez de plusieurs cartes de même type. Veuillez spécifier laquelle en précisant son numéro!");
            }
            plafondService.updateCardLimitByCardType(m.getUserId(), m.getCarte().getTypeCarte(),m.getPlafond().getMontantPlafond(), m.getPlafond().getTypePlafond(),m.getDuration(),m.getStatut());
            return ResponseEntity.status(HttpStatus.CREATED).body("Le plafond de la carte a été modifié avec succès!");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Aucune carte ne correspond à ce type de compte!");
        }
    }
    @PutMapping("/updateByCardNum")
    @Operation(summary = "Méthode pour modifier le plafond d'une carte d'un utilisateur par numéro de carte")
    public ResponseEntity<String> updateCardLimitByCardNum(@RequestBody ManageUserCardLimits m) {
        Optional<Carte> cartes = carteService.getCardByNum(m.getCarte().getNumeroCarte());
        if (cartes.isPresent()) {
            plafondService.updateCardLimitByCardNum(m.getCarte().getNumeroCarte(),m.getPlafond().getMontantPlafond(), m.getPlafond().getTypePlafond(),m.getDuration(),m.getStatut());
            return ResponseEntity.status(HttpStatus.CREATED).body("Le plafond de la carte a été modifié avec succès!");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Aucune carte ne correspond au numéro prononcé.");
        }
    }

}
