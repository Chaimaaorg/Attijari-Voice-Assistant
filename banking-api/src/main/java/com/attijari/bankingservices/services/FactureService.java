package com.attijari.bankingservices.services;

import com.attijari.bankingservices.models.Facture;

import java.util.Optional;

public interface FactureService {

    void updateInvoiceStatus(Long userId,Long beneficiaryId,String status);
    boolean checkInvoiceStatus(String numFacture);
    Optional<Facture> getInvoiceByNumber(String numeroFacture);
}
