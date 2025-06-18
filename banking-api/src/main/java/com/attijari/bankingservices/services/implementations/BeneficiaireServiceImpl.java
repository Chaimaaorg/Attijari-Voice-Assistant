package com.attijari.bankingservices.services.implementations;

import com.attijari.bankingservices.models.*;
import com.attijari.bankingservices.repositories.BeneficiaireRepository;
import com.attijari.bankingservices.repositories.UtilisateurRepository;
import com.attijari.bankingservices.services.BeneficiaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class BeneficiaireServiceImpl implements BeneficiaireService {

    private final BeneficiaireRepository beneficiaireRepository;
    private final UtilisateurRepository utilisateurRepository;

    @Autowired
    public BeneficiaireServiceImpl(BeneficiaireRepository beneficiaireRepository, UtilisateurRepository utilisateurRepository) {
        this.beneficiaireRepository = beneficiaireRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    @Override
    public void addBeneficiary(Long userId, String rib, String firstname, String lastname, String type) {
        Client client = Objects.requireNonNull(utilisateurRepository.findById(userId).orElse(null)).getClient();

        if (client != null) {
            Beneficiaire beneficiaire = new Beneficiaire();
            beneficiaire.setClient(client);
            beneficiaire.setNom(lastname);
            beneficiaire.setPrenom(firstname);
            beneficiaire.setRib(rib);
            beneficiaire.setTypeBeneficiaire(type);
            beneficiaireRepository.save(beneficiaire);
        }
    }

    @Override
    public List<Beneficiaire> getBeneficiaryByUserIdNames(Long userId, String firstname, String lastname) {
        // Retrieve the client associated with the provided userId
        Client client = utilisateurRepository.findById(userId)
                .map(Utilisateur::getClient)
                .orElse(null);

        if (client != null) {
            // Retrieve beneficiaries based on the client and names
            return beneficiaireRepository.findByClientIdAndNames(client.getIdClient(), firstname, lastname);
        } else {
            // If client not found, return an empty list
            return Collections.emptyList();
        }
    }


    @Override
    public void updateBeneficiaryByNames(Long userId, String firstname, String lastname, String rib) {
        Beneficiaire beneficiaire = getBeneficiaryByUserIdNames(userId, firstname, lastname).get(0);
        if (beneficiaire != null) {
            beneficiaire.setRib(rib);
            beneficiaireRepository.save(beneficiaire);
        }
    }

    @Override
    public void updateBeneficiaryByRib(Long userId, String oldRib, String newRib) {
        Client client = utilisateurRepository.findById(userId)
                .map(Utilisateur::getClient)
                .orElse(null);
        if(client!=null){
            Long clientId = client.getIdClient();
            Beneficiaire beneficiaire = beneficiaireRepository.findByClientIdAndRib(clientId, oldRib);
            if (beneficiaire != null) {
                beneficiaire.setRib(newRib);
                beneficiaireRepository.save(beneficiaire);
            }
        }

    }

    @Override
    public void deleteBeneficiaryByNames(Long userId, String firstname, String lastname) {
        Beneficiaire beneficiaire = getBeneficiaryByUserIdNames(userId, firstname, lastname).get(0);
        if (beneficiaire != null) {
            beneficiaireRepository.delete(beneficiaire);
        }
    }

    @Override
    public void deleteBeneficiaryByRib(Long userId, String rib) {
        Client client = utilisateurRepository.findById(userId)
                .map(Utilisateur::getClient)
                .orElse(null);
        if(client != null){
            Long clientId = client.getIdClient();
            Beneficiaire beneficiaire = beneficiaireRepository.findByClientIdAndRib(clientId, rib);
            if (beneficiaire != null) {
                beneficiaireRepository.delete(beneficiaire);
            }
        }
    }

    @Override
    public Beneficiaire getBeneficiaryByUserIdAndRib(Long userId, String rib) {
        // Retrieve the client associated with the provided userId
        Client client = utilisateurRepository.findById(userId)
                .map(Utilisateur::getClient)
                .orElse(null);

        if (client != null) {
            // Retrieve beneficiaries based on the client and names
            return beneficiaireRepository.findByClientIdAndRib(client.getIdClient(), rib);
        } else {
            // If client not found, return an empty list
            return null;

        }
    }

}
