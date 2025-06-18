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
public class Compte {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_compte")
    private Long idCompte;

    @Column(name = "numero_compte", unique = true, nullable = false, length = 20)
    private String numeroCompte;

    @Column(name = "solde", nullable = false, precision = 50, scale = 2)
    private BigDecimal solde;

    @Column(name = "type_compte", nullable = false, length = 50)
    private String typeCompte;

    @ManyToOne
    @JoinColumn(name = "id_client", nullable = false)
    private Client client;

    @ManyToOne
    @JoinColumn(name = "id_agence", nullable = false)
    private Agence agence;

    @Column(name = "date_ouverture", nullable = false)
    private Timestamp dateOuverture;

    @Column(name = "taux_interets", precision = 5, scale = 2)
    private BigDecimal tauxInterets;

    @Column(name = "statut_compte", nullable = false, length = 20)
    private String statutCompte;



}
