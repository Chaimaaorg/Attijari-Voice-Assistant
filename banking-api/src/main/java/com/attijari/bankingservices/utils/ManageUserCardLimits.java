package com.attijari.bankingservices.utils;

import com.attijari.bankingservices.models.Carte;
import com.attijari.bankingservices.models.Plafond;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Optional;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ManageUserCardLimits {
    Long userId;
    Carte carte;
    Plafond plafond;
    Optional<Timestamp> duration;
    String statut;

}



