package com.attijari.bankingservices.models;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.Date;
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "paiement_facture")
public class PaiementFacture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_paiement")
    private Long idPaiement;

    @OneToOne
    @JoinColumn(name = "id_facture", nullable = false, unique = true)
    private Facture facture;

    @ManyToOne
    @JoinColumn(name = "id_compte", nullable = false)
    private Compte compte;

    @Column(name = "date_paiement", nullable = false)
    private Timestamp datePaiement;
}