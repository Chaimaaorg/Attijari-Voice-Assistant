package com.attijari.bankingservices.services;

import com.attijari.bankingservices.models.Agence;

import java.math.BigDecimal;
import java.util.List;

public interface AgenceService {
    List<Agence> getAllAgencies();
    List<Agence> getAgenciesByUserId(Long userId);

    List<Agence> getAgenciesByRegistredAddress(Long userId);
    List<Agence> getAgenciesByUserLocation(BigDecimal lat,BigDecimal longitude);

}
