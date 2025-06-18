package com.attijari.bankingservices.services;

import com.attijari.bankingservices.models.Utilisateur;

import java.util.List;

public interface UtilisateurService {
    Utilisateur authenticateUser(String username, String password);
    Utilisateur getUserById(Long userId);
    List<Utilisateur> getAllUsers();
}
