package com.attijari.bankingservices.controllers;

import com.attijari.bankingservices.models.Client;
import com.attijari.bankingservices.services.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
@RestController
@RequestMapping("/clients")
@Tag(name = "Client", description = "Gestion des infos de clients")
public class ClientController {

    @Autowired
    private  ClientService clientService;

    @GetMapping("/{clientId}")
    @Operation(summary="Méthode pour récupérer un client par id d'utilisateur")
    public ResponseEntity<Client> getClientInfoById(@PathVariable Long clientId) {
        Client client = clientService.getClientById(clientId);
        if (client != null) {
            return ResponseEntity.ok(client);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}

