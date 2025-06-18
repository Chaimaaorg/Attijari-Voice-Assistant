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
public class Plafond {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_plafond")
    private Long idPlafond;

    @Column(name = "type_plafond", nullable = false, length = 50)
    private String typePlafond;

    @Column(name = "montant_plafond", nullable = false, precision = 15, scale = 2)
    private BigDecimal montantPlafond;

    @Column(name = "periode", nullable = false, length = 50)
    private String periode;

    @Column(name = "est_permanent", nullable = false)
    private boolean estPermanent;

    @Column(name = "date_debut", nullable = false)
    private Timestamp dateDebut;

    @Column(name = "date_expiration")
    private Timestamp dateExpiration;

    @ManyToOne
    @JoinColumn(name = "id_carte", nullable = false)
    private Carte carte;

}

