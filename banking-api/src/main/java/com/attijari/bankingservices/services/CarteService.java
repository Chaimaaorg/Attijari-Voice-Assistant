package com.attijari.bankingservices.services;

import com.attijari.bankingservices.models.Carte;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CarteService {
    List<Carte> getCardByUserId(Long userId);

    Optional<Carte> getCardByNum(Long cardNum);
    List<Carte>  getCardByUserIdAndType(Long userId,String typeCard);
    List<Carte> getCardByUserIdAndEntities(Long userId, Map<String,Object> entitiesDict);
    void updateCardServicesByCardNum(Long userId,Long cardNum,List<String> services,String status);
    void updateCardServicesByCardType(Long userId,String cardType,List<String> services,String status);
    void opposeCardByCardNum(Long userId,Long cardNum,String reasons);
    void opposeCardByCardType(Long userId,String cardType,String reasons);
}
