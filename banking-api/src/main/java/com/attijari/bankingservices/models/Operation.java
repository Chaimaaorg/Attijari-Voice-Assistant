package com.attijari.bankingservices.models;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Operation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_operation")
    private Long idOperation;

    @ManyToOne
    @JoinColumn(name = "id_compte", nullable = false)
    private Compte compte;

    @Column(name = "date_operation", nullable = false)
    private Timestamp dateOperation;

    @Column(name = "type_operation", nullable = false, length = 50)
    private String typeOperation;

    @Column(name = "montant", nullable = false, precision = 15, scale = 2)
    private BigDecimal montant;

    @ManyToOne
    @JoinColumn(name = "id_beneficiaire", nullable = false)
    private Beneficiaire beneficiaire;

    @Column(name = "motif", length = 255)
    private String motif;

    @Column(name = "categorie_operation", nullable = false, length = 100)
    private String categorieOperation;

}

