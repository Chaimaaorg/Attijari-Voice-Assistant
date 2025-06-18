package com.attijari.bankingservices.utils;

import com.attijari.bankingservices.models.Carte;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ManageUserCartes {
    Carte carte;
    Long userId;

    List<String> services;
    String raisonsOpposition;
    String status;
}