package com.attijari.bankingservices.models;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Utilisateur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private Long idUser;

    @Column(name = "username", unique = true, nullable = false, length = 25)
    private String username;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "telephone", unique = true, nullable = false, length = 15)
    private String telephone;


    @Column(name = "chemin_audio", nullable = false)
    private String cheminAudio;
    @OneToOne
    @JoinColumn(name = "idClient", nullable = false)
    private Client client;

}

