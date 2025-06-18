package com.attijari.bankingservices.models;
import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Agence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_agence")
    private Long idAgence;

    @Column(name = "nom_agence", nullable = false, length = 100)
    private String nomAgence;

    @Column(name = "adresse", nullable = false, length = 255)
    private String adresse;

    @Column(name = "telephone", unique = true, nullable = false, length = 15)
    private String telephone;

    @Column(name = "horaires_ouverture", length = 100)
    private String horairesOuverture;

    @Column(name = "services_disponibles", length = 255)
    private String servicesDisponibles;
}
