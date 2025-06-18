-- Création de la table Client
CREATE TABLE Client (
    id_client bigserial PRIMARY KEY,
    cin VARCHAR(15) NOT NULL UNIQUE,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    genre VARCHAR(10) NOT NULL,
    date_naissance DATE NOT NULL,
    lieu_naissance VARCHAR(100) NOT NULL,
    adresse VARCHAR(255) NOT NULL
);
-- Création de la table Utilisateur
CREATE TABLE Utilisateur (
    id_user bigserial PRIMARY KEY,
    username VARCHAR(25) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    telephone VARCHAR(15) NOT NULL UNIQUE,
    -- voice_features FLOAT[],
    chemin_audio varchar(255) NOT NULL,
    id_client INT NOT NULL UNIQUE,
    FOREIGN KEY (id_client) REFERENCES Client(id_client)
);
-- Création de la table Agence
CREATE TABLE Agence (
    id_agence bigserial PRIMARY KEY,
    nom_agence VARCHAR(100) NOT NULL,
    adresse VARCHAR(255) NOT NULL,
    telephone VARCHAR(15) NOT NULL UNIQUE,
    horaires_ouverture VARCHAR(100),
    services_disponibles VARCHAR(255)
    
);

-- Création de la table Compte
CREATE TABLE Compte (
    id_compte bigserial PRIMARY KEY,
    numero_compte VARCHAR(20) NOT NULL UNIQUE,
    solde DECIMAL(50, 2) NOT NULL,
    type_compte VARCHAR(50) NOT NULL,
    id_client INT NOT NULL,
    id_agence INT NOT NULL,
    date_ouverture TIMESTAMP NOT NULL,
    taux_interets DECIMAL(5, 2),
    statut_compte VARCHAR(20) NOT NULL,
    FOREIGN KEY (id_client) REFERENCES Client(id_client),
    FOREIGN KEY (id_agence) REFERENCES Agence(id_agence)
);

-- Création de la table Carte
CREATE TABLE Carte (
    id_carte bigserial PRIMARY KEY,
    id_compte INT NOT NULL,
    type_carte VARCHAR(50) NOT NULL,
    numero_carte bigint NOT NULL UNIQUE,
    date_expiration TIMESTAMP NOT NULL,
    code_pin VARCHAR(4) NOT NULL,
    date_activation TIMESTAMP NOT NULL,
    date_opposition TIMESTAMP,
    cvv VARCHAR(3) NOT NULL UNIQUE,
    statut_carte VARCHAR(20) NOT NULL,
    services VARCHAR(255) NOT NULL,
    raisons_opposition VARCHAR(255) NOT NULL,
    FOREIGN KEY (id_compte) REFERENCES Compte(id_compte)
);
-- Création de la table Plafond
CREATE TABLE Plafond (
    id_plafond bigserial PRIMARY KEY,
    type_plafond VARCHAR(50) NOT NULL,
    montant_plafond DECIMAL(15,2) NOT NULL,
    periode VARCHAR(50) NOT NULL, -- Par jour il ne doit pas dépasser..
    est_permanent BOOLEAN NOT NULL DEFAULT false, -- Utilisation de BOOLEAN pour indiquer si le plafond est permanent
    date_debut TIMESTAMP NOT NULL, -- Date de début 
    date_expiration TIMESTAMP, -- Date de d'expiration pour les plafonds temporaires
    id_carte INT NOT NULL,
    FOREIGN KEY (id_carte) REFERENCES Carte(id_carte)
);
-- Création de la table Beneficiaire
CREATE TABLE Beneficiaire (
    id_beneficiaire bigserial PRIMARY KEY,
    id_client INT NOT NULL,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    rib VARCHAR(50) NOT NULL UNIQUE,
    type_beneficiaire VARCHAR(50),
    FOREIGN KEY (id_client) REFERENCES Client(id_client)
);
-- Création de la table Opération
CREATE TABLE Operation (
    id_operation bigserial PRIMARY KEY,
    id_compte INT NOT NULL,
    date_operation TIMESTAMP NOT NULL,
    type_operation VARCHAR(50) NOT NULL,
    montant DECIMAL(15, 2) NOT NULL,
    id_beneficiaire INT NOT NULL,
    motif VARCHAR(255),
    categorie_operation VARCHAR(100) NOT NULL,
    FOREIGN KEY (id_compte) REFERENCES Compte(id_compte),
    FOREIGN KEY (id_beneficiaire) REFERENCES Beneficiaire(id_beneficiaire)
);


-- Création de la table Facture
CREATE TABLE Facture (
    id_facture bigserial PRIMARY KEY,
    numero_facture VARCHAR(20) NOT NULL UNIQUE,
    montant DECIMAL(15, 2) NOT NULL,
    date_emission TIMESTAMP NOT NULL,
    date_echeance TIMESTAMP NOT NULL,
    statut_paiement VARCHAR(20) NOT NULL,
    id_beneficiaire INT NOT NULL,
    id_client INT NOT NULL,
    FOREIGN KEY (id_beneficiaire) REFERENCES Beneficiaire(id_beneficiaire),
    FOREIGN KEY (id_client) REFERENCES Client(id_client)
);

-- Création de la table PaiementFacture
CREATE TABLE paiement_facture (
    id_paiement bigserial PRIMARY KEY,
    id_facture INT NOT NULL UNIQUE,
    id_compte INT NOT NULL,
    date_paiement TIMESTAMP NOT NULL,
    FOREIGN KEY (id_facture) REFERENCES Facture(id_facture),
    FOREIGN KEY (id_compte) REFERENCES Compte(id_compte)
);
