-- Insertions dans la table Client
INSERT INTO Client (id_client,cin, nom, prenom, genre, date_naissance, lieu_naissance, adresse) VALUES
(1,'ab123456', 'Marouani', 'Amine', 'homme', '1990-05-15', 'paris', '12 rue de la liberte'),
(2,'cd789012', 'Ourgani', 'Chaimaa', 'femme', '1985-08-20', 'lyon', '8 avenue des roses');

-- Insertions dans la table Utilisateur
INSERT INTO Utilisateur (username, password, email, telephone,chemin_audio, id_client) VALUES
('amine', 'password', 'amarouani@mail.ma', '0612345678','C:\Users\HP\Documents\docs pfe\datasets\audios\amine.wav', 1),
('chaima', 'password', 'courgani@mail.ma', '0641974324','C:\Users\HP\Documents\docs pfe\datasets\audios\chaimaa.wav',2);

-- Insertions dans la table Agence
INSERT INTO Agence (id_agence,nom_agence, adresse, telephone, horaires_ouverture, services_disponibles) VALUES
(1,'agence paris centre', '5 rue de rivoli, paris', '0123456789', 'lun-ven: 9h-18h', 'retrait, dépôt, conseil'),
(2,'agence lyon est', '10 avenue des fleurs, lyon', '0234567890', 'lun-ven: 8h-17h', 'retrait, dépôt, change');

-- Insertions dans la table Compte
INSERT INTO Compte (id_compte,numero_compte, solde, type_compte, id_client, id_agence, date_ouverture, taux_interets, statut_compte) VALUES
(1,'000123P56789012', 5000.00, 'courant', 1, 1, CURRENT_TIMESTAMP, 0.05, 'actif'),
(2,'000987L50987654', 8000.00, 'epargne', 2, 2, CURRENT_TIMESTAMP, 0.1, 'actif');

-- Insertions dans la table Carte
INSERT INTO Carte (id_carte,id_compte, type_carte, numero_carte, date_expiration, code_pin, date_activation, date_opposition, cvv, statut_carte, services, raisons_opposition) VALUES
(1,1, 'visa electron web pay', 1234567890123456, '2026-05-01', '1234', CURRENT_TIMESTAMP, NULL, '123', 'active', 'paiement en ligne, retrait dab', ''),
(2,2, 'mastercard mourih', 9876543210987654, '2027-06-01', '4321', CURRENT_TIMESTAMP, NULL, '456', 'active', 'paiement en magasin, retrait dab', '');

-- Insertions dans la table Plafond
INSERT INTO Plafond (id_plafond,type_plafond, montant_plafond, periode, est_permanent, date_debut, date_expiration, id_carte) VALUES
(1,'retraits', 300.00, 'par jour', true, CURRENT_TIMESTAMP, NULL, 1),
(2,'paiements', 1000.00, 'par mois', false, CURRENT_TIMESTAMP, '2024-12-31', 1),
(3,'retraits', 200.00, 'par jour', true, CURRENT_TIMESTAMP, NULL, 2),
(4,'paiements', 500.00, 'par mois', false, CURRENT_TIMESTAMP, '2024-12-31', 2);

-- Insertions dans la table Beneficiaire
INSERT INTO Beneficiaire (id_beneficiaire,id_client, nom, prenom, rib, type_beneficiaire) VALUES
(1,1, 'martin', 'sophie', '0123 4567 8901 2345 6789 0123', 'physique'),
(2,2, 'leclerc', 'thomas', '6789 0123 4567 8901 2345 6789', 'morale');

-- Insertions dans la table Opération
INSERT INTO Operation (id_operation,id_compte, date_operation, type_operation, montant, id_beneficiaire, motif, categorie_operation) VALUES
(1,1, CURRENT_TIMESTAMP, 'dépôt', 2000.00, 1, 'salaire', 'retrait'),
(2,1, CURRENT_TIMESTAMP, 'virement', 500.00, 2, 'remboursement', 'permanent'),
(3,2, CURRENT_TIMESTAMP, 'dépôt', 1000.00, 1, 'epargne mensuelle', 'epargne');

-- Insertions dans la table Facture
INSERT INTO Facture (id_facture,numero_facture, montant, date_emission, date_echeance, statut_paiement, id_beneficiaire, id_client) VALUES
(1,'INV12345678901234', 250.00, CURRENT_TIMESTAMP, '2024-05-01', 'non payee', 1, 1),
(2,'INV56789012345678', 150.00, CURRENT_TIMESTAMP, '2024-05-05', 'non payee', 2, 2);

-- Insertions dans la table PaiementFacture
INSERT INTO paiement_facture (id_paiement,id_facture, id_compte, date_paiement) VALUES
(1,1, 1, CURRENT_TIMESTAMP),
(2,2, 2, CURRENT_TIMESTAMP);

