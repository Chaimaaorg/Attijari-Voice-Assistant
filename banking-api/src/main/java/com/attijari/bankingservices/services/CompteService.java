package com.attijari.bankingservices.services;

import com.attijari.bankingservices.models.Compte;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CompteService {

    List<Compte> getAccountByUserID(Long userId);
    List<Compte> getAccountByUserIdAndAccountType(Long userID,String accountType);

    Optional<Compte> getAccountByUserIdAndAccountNum(Long userID, String accountNum);

    List<Compte> getAccountsByUserIdAndEntities(Long userId, Map<String,Object> entitiesDict);

    Map<BigDecimal,Compte> getBalanceByUserIdAndEntities(Long userId, Map<String,Object> entitiesDict);

    Long getAccountUserId(Long idCompte);

}
