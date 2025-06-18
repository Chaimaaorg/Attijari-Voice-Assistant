package com.attijari.bankingservices.controllers;

import com.attijari.bankingservices.models.Compte;
import com.attijari.bankingservices.models.Facture;
import com.attijari.bankingservices.services.CompteService;
import com.attijari.bankingservices.services.FactureService;
import com.attijari.bankingservices.services.PaiementFactureService;
import com.attijari.bankingservices.utils.ManageUserInvoicePayments;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/paiement-facture")
@Tag(name = "Paiements de factures", description = "Gestion des paiements des clients")
public class PaiementFactureController {

    private final PaiementFactureService paiementFactureService;
    private final CompteService compteService;
    private final FactureService factureService;

    @Autowired
    public PaiementFactureController(PaiementFactureService paiementFactureService, CompteService compteService, FactureService factureService) {
        this.paiementFactureService = paiementFactureService;
        this.compteService = compteService;
        this.factureService = factureService;
    }

    @PostMapping("/addInvoiceByAccountType")
    @Operation(summary = "Méthode pour ajouter le paiement d'une transaction par numéro de compte bancaire")
    public ResponseEntity<String> addTransactionByAccountType(@RequestBody ManageUserInvoicePayments m) {
        Optional<Facture> invoice = factureService.getInvoiceByNumber(m.getNumeroFacture());
        if (invoice.isPresent()) {
            List<Compte> accounts = compteService.getAccountByUserIdAndAccountType(m.getUserId(), m.getTypeCompte());
            if (!accounts.isEmpty()) {
                if (accounts.size() > 1) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Veuillez préciser le numéro de compte car vous disposez de plusieurs comptes du même type.");
                } else {
                    if (accounts.get(0).getStatutCompte().equals("actif")) {
                        BigDecimal balance = accounts.get(0).getSolde();
                        BigDecimal amount = invoice.get().getMontant();
                        int comparisonResult = balance.compareTo(amount);
                        if (comparisonResult < 0) {
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Votre solde courant est insuffisant pour effectuer ce paiement!");
                        } else {
                            paiementFactureService.addInvoiceTransactionByAccountType(m.getUserId(), m.getNumeroFacture(), m.getTypeCompte());
                            return ResponseEntity.status(HttpStatus.CREATED).body("Paiement de facture ajouté avec succès.");
                        }
                    } else {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ce compte est fermé, vous ne pouvez pas passer ce paiement!");
                    }
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Aucun compte ne correspond à ce numéro!");
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Numéro de facture introuvable. Veuillez le vérifier et le préciser à nouveau.");
        }
    }

    @PostMapping("/addInvoiceByAccountNum")
    @Operation(summary = "Méthode pour ajouter le paiement d'une transaction par type de compte bancaire")
    public ResponseEntity<String> addTransactionByAccountNum(@RequestBody ManageUserInvoicePayments m) {
        Optional<Facture> invoice = factureService.getInvoiceByNumber(m.getNumeroFacture());
        if (invoice.isPresent()) {
            Optional<Compte> accounts = compteService.getAccountByUserIdAndAccountNum(m.getUserId(), m.getNumeroCompte());
            if (accounts.isPresent()) {
                if(accounts.get().getStatutCompte().equals("actif")){
                    BigDecimal balance = accounts.get().getSolde();
                    BigDecimal amount = invoice.get().getMontant();
                    int comparisonResult = balance.compareTo(amount);
                    if (comparisonResult < 0) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Votre solde courant est insuffisant pour effectuer ce paiement!");
                    } else {
                        paiementFactureService.addInvoiceTransactionByAccountNum(m.getNumeroFacture(), m.getNumeroCompte());
                        return ResponseEntity.status(HttpStatus.CREATED).body("Paiement de facture ajouté avec succès.");
                    }
                }
                else{
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ce compte est fermé, vous ne pouvez pas passer ce paiement!");
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Aucun compte ne correspond à ce type de compte!");
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Numéro de facture introuvable. Veuillez le vérifier et le préciser à nouveau.");
        }

    }
}
