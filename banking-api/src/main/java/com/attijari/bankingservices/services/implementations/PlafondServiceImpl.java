package com.attijari.bankingservices.services.implementations;

import com.attijari.bankingservices.models.Carte;
import com.attijari.bankingservices.models.Plafond;
import com.attijari.bankingservices.repositories.CarteRepository;
import com.attijari.bankingservices.repositories.FactureRepository;
import com.attijari.bankingservices.repositories.PlafondRepository;
import com.attijari.bankingservices.services.PlafondService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class PlafondServiceImpl implements PlafondService {

    private final PlafondRepository plafondRepository;
    private final CarteRepository carteRepository;
    private final FactureRepository factureRepository;

    @Autowired
    public PlafondServiceImpl(PlafondRepository plafondRepository,
                              CarteRepository carteRepository,
                              FactureRepository factureRepository) {
        this.plafondRepository = plafondRepository;
        this.carteRepository = carteRepository;
        this.factureRepository = factureRepository;
    }

    @Override
    public void updateCardLimitByCardNum(Long cardNum, BigDecimal limit, String typeLimit, Optional<Timestamp> duration, String status) {
        Optional<Carte> card = carteRepository.findByNumeroCarte(cardNum);
        if (card.isPresent()) {
            Carte carte = card.get();
            List<Plafond> existingPlafonds = plafondRepository.findByCarte(carte.getIdCarte());
            boolean foundMatchingType = false;

            for (Plafond plafond : existingPlafonds) {
                if (plafond.getTypePlafond().equals(typeLimit)) {
                    foundMatchingType = true;

                    BigDecimal newLimit = (status.equals("add")) ? plafond.getMontantPlafond().add(limit) :  plafond.getMontantPlafond().compareTo(limit)<0 ? limit :plafond.getMontantPlafond().subtract(limit);
                    plafond.setMontantPlafond(newLimit);
                    plafond.setDateDebut(new Timestamp(System.currentTimeMillis()));
                    if (duration.isPresent()) {
                        plafond.setEstPermanent(false);
                        plafond.setDateExpiration(duration.get());
                    } else {
                        plafond.setEstPermanent(true);
                        plafond.setDateExpiration(null);
                    }
                    plafondRepository.save(plafond);
                    break;
                }
            }

            if (!foundMatchingType) {
                Plafond newPlafond = new Plafond();
                newPlafond.setTypePlafond(typeLimit);
                newPlafond.setMontantPlafond(limit);
                newPlafond.setPeriode("Par jour");
                newPlafond.setDateDebut(new Timestamp(System.currentTimeMillis()));
                newPlafond.setCarte(carte);
                if (duration.isPresent()) {
                    newPlafond.setEstPermanent(false);
                    newPlafond.setDateExpiration(duration.get());
                } else {
                    newPlafond.setEstPermanent(true);
                    newPlafond.setDateExpiration(null);
                }
                plafondRepository.save(newPlafond);
            }
        } else {
            System.out.println("Carte non retrouvée");
        }
    }


    @Override
    public void updateCardLimitByCardType(Long userID, String cardType, BigDecimal limit, String typeLimit, Optional<Timestamp> duration, String status) {
        List<Carte> cartes = carteRepository.findByTypeCarte(cardType);
        if (!cartes.isEmpty()) {
            Carte carte = cartes.get(0); // Supposant qu'on traite avec la première carte trouvée du type donné
            List<Plafond> existingPlafonds = plafondRepository.findByCarte(carte.getIdCarte());
            boolean foundMatchingType = false;

            for (Plafond plafond : existingPlafonds) {
                if (plafond.getTypePlafond().equals(typeLimit)) {
                    foundMatchingType = true;
                    BigDecimal newLimit = (status.equals("add")) ? plafond.getMontantPlafond().add(limit) :  plafond.getMontantPlafond().compareTo(limit)<0 ? limit :plafond.getMontantPlafond().subtract(limit);
                    plafond.setMontantPlafond(newLimit);
                    plafond.setDateDebut(new Timestamp(System.currentTimeMillis()));
                    if (duration.isPresent()) {
                        plafond.setEstPermanent(false);
                        plafond.setDateExpiration(duration.get());
                    } else {
                        plafond.setEstPermanent(true);
                        plafond.setDateExpiration(null);
                    }
                    plafondRepository.save(plafond);
                    break;
                }
            }

            if (!foundMatchingType) {
                // Création d'un nouveau plafond si aucun plafond correspondant n'a été trouvé
                Plafond newPlafond = new Plafond();
                newPlafond.setTypePlafond(typeLimit);
                newPlafond.setMontantPlafond(limit);
                newPlafond.setPeriode("Par jour");
                newPlafond.setDateDebut(new Timestamp(System.currentTimeMillis()));
                newPlafond.setCarte(carte);
                if (duration.isPresent()) {
                    newPlafond.setEstPermanent(false);
                    newPlafond.setDateExpiration(duration.get());
                } else {
                    newPlafond.setEstPermanent(true);
                    newPlafond.setDateExpiration(null);
                }
                plafondRepository.save(newPlafond);
            }
        } else {
            System.out.println("Aucune carte de ce type n'a été trouvée.");
        }
    }
}
