package com.attijari.bankingservices.models;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Carte {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_carte")
    private Long idCarte;

    @ManyToOne
    @JoinColumn(name = "id_compte", nullable = false)
    private Compte compte;

    @Column(name = "type_carte", nullable = false, length = 50)
    private String typeCarte;

    @Column(name = "numero_carte", unique = true, nullable = false)
    private Long numeroCarte;

    @Column(name = "date_expiration", nullable = false)
    private Timestamp dateExpiration;

    @Column(name = "code_pin", nullable = false, length = 4)
    private String codePin;

    @Column(name = "date_activation", nullable = false)
    private Timestamp dateActivation;

    @Column(name = "date_opposition")
    private Timestamp dateOpposition;

    @Column(name = "cvv", unique = true, nullable = false, length = 3)
    private String cvv;

    @Column(name = "statut_carte", nullable = false, length = 20)
    private String statutCarte;

    @Column(name = "services", nullable = false, length = 255)
    private String services;

    @Column(name = "raisons_opposition", nullable = false, length = 255)
    private String raisonsOpposition;

}

