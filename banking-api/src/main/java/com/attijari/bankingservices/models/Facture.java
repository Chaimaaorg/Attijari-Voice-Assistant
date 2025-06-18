package com.attijari.bankingservices.models;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Facture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_facture")
    private Long idFacture;

    @Column(name = "numero_facture", nullable = false, unique = true, length = 20)
    private String numeroFacture;

    @Column(name = "montant", nullable = false, precision = 15, scale = 2)
    private BigDecimal montant;

    @Column(name = "date_emission", nullable = false)
    private Timestamp dateEmission;

    @Column(name = "date_echeance", nullable = false)
    private Timestamp dateEcheance;

    @Column(name = "statut_paiement", nullable = false, length = 20)
    private String statutPaiement;

    @ManyToOne
    @JoinColumn(name = "id_beneficiaire", nullable = false)
    private Beneficiaire beneficiaire;

    @ManyToOne
    @JoinColumn(name = "id_client", nullable = false)
    private Client client;

}