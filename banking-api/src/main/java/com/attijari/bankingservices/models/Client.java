package com.attijari.bankingservices.models;

import jakarta.persistence.*;

import lombok.*;

import java.sql.Date;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_client")
    private Long idClient;

    @Column(name = "cin", unique = true, nullable = false, length = 15)
    private String cin;

    @Column(name = "nom", nullable = false, length = 50)
    private String nom;

    @Column(name = "prenom", nullable = false, length = 50)
    private String prenom;

    @Column(name = "genre", nullable = false, length = 10)
    private String genre;

    @Column(name = "date_naissance", nullable = false)
    private Date dateNaissance;

    @Column(name = "lieu_naissance", nullable = false, length = 100)
    private String lieuNaissance;

    @Column(name = "adresse", nullable = false, length = 255)
    private String adresse;

}
