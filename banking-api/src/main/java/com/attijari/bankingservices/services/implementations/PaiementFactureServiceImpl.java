package com.attijari.bankingservices.services.implementations;

import com.attijari.bankingservices.models.*;
import com.attijari.bankingservices.repositories.*;
import com.attijari.bankingservices.services.PaiementFactureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class PaiementFactureServiceImpl implements PaiementFactureService {

    private final PaiementFactureRepository paiementFactureRepository;
    private final CompteRepository compteRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final FactureRepository factureRepository;

    @Autowired
    public PaiementFactureServiceImpl(PaiementFactureRepository paiementFactureRepository, UtilisateurRepository utilisateurRepository, CompteRepository compteRepository, UtilisateurRepository utilisateurRepository1, BeneficiaireRepository beneficiaireRepository, FactureRepository factureRepository) {
        this.paiementFactureRepository = paiementFactureRepository;
        this.compteRepository = compteRepository;
        this.utilisateurRepository = utilisateurRepository1;
        this.factureRepository = factureRepository;
    }

    @Override
    public void addInvoiceTransactionByAccountType(Long userId, String invoiceNum, String accountType) {
        PaiementFacture paiementFacture = new PaiementFacture();
        Optional<Facture> facture = factureRepository.findByNumeroFacture(invoiceNum);
        Utilisateur user = utilisateurRepository.findById(userId).orElse(null);
        if(user!=null){
            if(facture.isPresent())
            {
                List<Compte> comptes =  compteRepository.findByClientId(user.getClient().getIdClient());
                if(comptes!=null){
                    for(Compte compte: comptes){
                        if(compte.getTypeCompte().equals(accountType))
                        {
                            BigDecimal balance = compte.getSolde();
                            BigDecimal newBalance = balance.subtract(facture.get().getMontant());
                            compte.setSolde(newBalance);
                            compteRepository.save(compte);
                            paiementFacture.setFacture(facture.get());
                            paiementFacture.setDatePaiement(new Timestamp(System.currentTimeMillis()));
                            paiementFacture.setCompte(compte);
                            paiementFactureRepository.save(paiementFacture);
                        }
                    }
                }
            }
            else{
                System.out.println("Invoice not found");
            }
        }
        else {
            System.out.println("User not found");
        }

    }

    @Override
    public void addInvoiceTransactionByAccountNum(String invoiceNum, String accountNum) {
        PaiementFacture paiementFacture = new PaiementFacture();
        Optional<Facture> facture = factureRepository.findByNumeroFacture(invoiceNum);
        if(facture.isPresent())
        {
            Compte compte = compteRepository.findByNumeroCompte(accountNum);
            if(compte!=null){
                BigDecimal balance = compte.getSolde();
                BigDecimal newBalance = balance.subtract(facture.get().getMontant());
                compte.setSolde(newBalance);
                compteRepository.save(compte);
                paiementFacture.setFacture(facture.get());
                paiementFacture.setDatePaiement(new Timestamp(System.currentTimeMillis()));
                paiementFacture.setCompte(compte);
                paiementFactureRepository.save(paiementFacture);
            }
        }
    }
}
