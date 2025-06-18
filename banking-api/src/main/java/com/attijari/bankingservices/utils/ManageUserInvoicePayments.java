package com.attijari.bankingservices.utils;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ManageUserInvoicePayments {
    Long userId;
    String numeroFacture;
    String numeroCompte;
    String typeCompte;
}
