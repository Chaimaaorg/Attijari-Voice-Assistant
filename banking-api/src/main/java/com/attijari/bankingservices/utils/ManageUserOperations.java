package com.attijari.bankingservices.utils;

import com.attijari.bankingservices.models.Beneficiaire;
import com.attijari.bankingservices.models.Compte;
import com.attijari.bankingservices.models.Operation;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Optional;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ManageUserOperations {
    Long userId;
    Compte compte;
    Operation operation;
    Beneficiaire beneficiaire;

}