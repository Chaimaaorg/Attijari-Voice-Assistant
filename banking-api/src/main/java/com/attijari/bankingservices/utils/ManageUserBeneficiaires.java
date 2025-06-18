package com.attijari.bankingservices.utils;

import com.attijari.bankingservices.models.Beneficiaire;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ManageUserBeneficiaires {
    Beneficiaire beneficiaire;
    Long userId;

    String oldRib;
    String newRib;


}
