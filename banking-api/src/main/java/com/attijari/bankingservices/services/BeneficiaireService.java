package com.attijari.bankingservices.services;

import com.attijari.bankingservices.models.Beneficiaire;

import java.util.List;

public interface BeneficiaireService {

    void addBeneficiary(Long userId,String rib,String firstname,String lastname,String type);
    List<Beneficiaire> getBeneficiaryByUserIdNames(Long userId, String firstname, String lastname);
    void updateBeneficiaryByNames(Long userId,String firstname, String lastname,String rib);
    void updateBeneficiaryByRib(Long userId,String oldRib,String newRib);
    void deleteBeneficiaryByNames(Long userId,String firstname,String lastname);
    void deleteBeneficiaryByRib(Long userId,String rib);

    Beneficiaire getBeneficiaryByUserIdAndRib(Long userId,String rib);
}
