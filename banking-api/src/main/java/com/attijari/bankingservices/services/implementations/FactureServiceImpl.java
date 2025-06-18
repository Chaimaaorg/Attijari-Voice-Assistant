package com.attijari.bankingservices.services.implementations;

import com.attijari.bankingservices.models.Client;
import com.attijari.bankingservices.models.Facture;
import com.attijari.bankingservices.models.Utilisateur;
import com.attijari.bankingservices.repositories.FactureRepository;
import com.attijari.bankingservices.repositories.UtilisateurRepository;
import com.attijari.bankingservices.services.FactureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class FactureServiceImpl implements FactureService {

    private final FactureRepository factureRepository;
    private final UtilisateurRepository utilisateurRepository;

    @Autowired
    public FactureServiceImpl(FactureRepository factureRepository, UtilisateurRepository utilisateurRepository) {
        this.factureRepository = factureRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    @Override
    public void updateInvoiceStatus(Long userId, Long beneficiaryId, String status) {
        List<Facture> invoiceList = factureRepository.findAll();
        Utilisateur user = utilisateurRepository.findById(userId).orElse(null);
        if(user !=null){
            Client client = user.getClient();
            if(client!=null){
                for (Facture invoice : invoiceList) {
                    if (invoice.getBeneficiaire().getIdBeneficiaire().equals(beneficiaryId) && invoice.getClient().getIdClient().equals(client.getIdClient())) {
                        invoice.setStatutPaiement(status);
                        factureRepository.save(invoice);
                    }
                }
            }
        }
    }

    @Override
    public boolean checkInvoiceStatus(String numFacture) {
        Optional<Facture> facture = factureRepository.findByNumeroFacture(numFacture);
        return facture.map(value -> value.getStatutPaiement().equals("payée")).orElse(false);
    }

    @Override
    public Optional<Facture> getInvoiceByNumber(String numFacture) {
        return factureRepository.findByNumeroFacture(numFacture);
    }

}
