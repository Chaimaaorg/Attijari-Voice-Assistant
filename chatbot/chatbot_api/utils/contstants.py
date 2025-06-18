# Constant NLU
import joblib
import torch
from transformers import AutoTokenizer, CamembertForSequenceClassification
import os
from dotenv import load_dotenv
from chatbot_api.utils.util_functions import extract_info

load_dotenv()

# Lire les variables d'environnement
MODEL_PATH = os.getenv("MODEL_PATH")
LABEL_ENCODER_PATH = os.getenv("LABEL_ENCODER_PATH")
GOOGLE_CLOUD_API = os.getenv("GOOGLE_CLOUD_API")
HF_TOKEN = os.getenv("HF_TOKEN")
SPRING_BASE_URL = os.getenv("SPRING_BASE_URL")
USERNAME = os.getenv("USERNAME")
PASSWORD = os.getenv("PASSWORD")
ROLE = os.getenv("ROLE")

MODEL = CamembertForSequenceClassification.from_pretrained("camembert-base", num_labels=18)
MODEL.load_state_dict(torch.load(MODEL_PATH, map_location=torch.device('cpu')))
TOKENIZER = AutoTokenizer.from_pretrained("camembert-base")
LABEL_ENCODER = joblib.load(LABEL_ENCODER_PATH)

CANCEL_ACTION = [
    "D'accord votre demande a été annulée. Que puis-je faire d'autre pour vous aujourd'hui ?",
    "Entendu, l'action a été annulé. Si vous avez d'autres questions ou besoins, n'hésitez pas à demander."
    "Compris, votre demande a été annulée. Si vous avez besoin de plus d'assistance ou si vous souhaitez effectuer "
    "une autre action, je suis là pour vous aider.",
    "Aucun problème, j'annule l'action . N'hésitez pas à me dire si vous avez besoin d'aide pour autre chose.",
    "Bien reçu, l'action a été annulée. Si vous avez d'autres demandes, je suis à votre disposition."
]
ASSISTANCE_ACTION = [
    "Besoin d'aide ? Contactez notre équipe dédiée au 05 22 58 88 55 ou par email à attijarinet@attijariwafa.com. Nous "
    "sommes là pour vous du lundi au samedi, de 8h à 20h (9h à 15h pendant Ramadan).",
    "Pour toute demande, notre équipe est là pour vous aider. Appelez-nous au 05 22 58 88 55 ou envoyez un email à "
    "attijarinet@attijariwafa.com. Nos horaires sont du lundi au samedi, de 8h à 20h (9h à 15h pendant Ramadan).",
    "Vous avez des questions ? Contactez-nous au 05 22 58 88 55 ou par email à attijarinet@attijariwafa.com. Nous "
    "sommes"
    "disponibles du lundi au samedi, de 8h à 20h (9h à 15h pendant Ramadan).",
    "Besoin d'assistance ? Appelez-nous au 05 22 58 88 55 ou envoyez un email à attijarinet@attijariwafa.com. Nos "
    "horaires sont du lundi au samedi, de 8h à 20h (9h à 15h pendant Ramadan).",
    "Pour toute demande d'aide, contactez notre équipe au 05 22 58 88 55 ou par email à attijarinet@attijariwafa.com. "
    "Nos horaires sont du lundi au samedi, de 8h à 20h (9h à 15h pendant Ramadan)."
]
# Security questions
QUESTION_TO_FUNCTION = {
    # "Pouvez-vous fournir votre clé de RIB ?": provide_account_number,
    # "Pouvez-vous fournir le code PIN de votre carte crédit?": provide_card_number,
    "Quelle est votre adresse e-mail associée à votre compte bancaire ?": "provide_email_address",
    # "Quelle est votre adresse postale enregistrée ?": "provide_postal_address",
    "Quelle est votre date de naissance ?": "provide_date_of_birth",
    # "Quel est le montant de votre dernier dépôt ou retrait ?": "provide_last_transaction_amount",
    "Quel est le nom de votre agence bancaire ?": "provide_agency",
    "Pouvez-vous fournir un numéro de téléphone associé à votre compte ?": "provide_phone_number"
}
# Greet responses
GREET_RESPONSES = [
    "Bonjour! Avant de commencer, je vais devoir vous poser quelques questions de sécurité pour confirmer votre "
    "identité.",
    "Bonjour! Avant de continuer, veuillez répondre à quelques questions de sécurité pour protéger votre compte.",
    "Salut! Avant de poursuivre, je dois m'assurer que vous êtes bien la bonne personne.",
    "Bonjour! Avant de discuter de vos besoins bancaires, je dois d'abord vérifier votre identité.",
    "Bonjour! Pour des raisons de sécurité, je vais devoir vous poser quelques questions rapides avant de continuer.",
    "Bonjour! Pour garantir la sécurité de votre compte, je vais d'abord devoir vérifier quelques détails avec vous."
]
# Security Question Success
LOGIN_SUCCESS_RESPONSES = [
    "Merci! Que puis-je faire pour vous aujourd'hui?",
    "Je vous remercie ! Comment puis-je vous aider aujourd'hui ?",
    "Merci ! Quel est le motif de votre visite aujourd'hui ?",
    "Merci ! Y a-t-il quelque chose que je peux faire pour vous aujourd'hui ?",
    "Je vous remercie. Comment puis-je vous être utile aujourd'hui ?"
]
# Security Question Failure
LOGIN_FAIL_RESPONSES = [
    "Désolé, vous n'êtes pas autorisé à accéder à ce service.",
    "Je suis désolé, mais vous n'avez pas l'autorisation d'accéder à ce service.",
    "Navré, l'accès à ce service vous est refusé.",
    "Pardon, mais vous n'êtes pas habilité à utiliser ce service.",
    "Malheureusement, vous ne disposez pas des droits nécessaires pour accéder à ce service."
]
# Fallback responses
FALLBACK_RESPONSES = [
    "Je ne suis pas sûr d'avoir compris ce que vous voulez dire. Pourriez-vous reformuler votre question ?",
    "J'ai besoin de plus d'informations pour comprendre votre demande. Pouvez-vous être plus précis ?",
    "Je suis encore en apprentissage. Pouvez-vous essayer de demander d'une autre manière ?",
    "Il semble que je manque de contexte. Pouvez-vous fournir des détails supplémentaires sur votre demande ?",
    "Je n'ai pas compris quelle opération vous souhaitez effectuer. Pouvez-vous s'il vous plaît me donner plus de "
    "détails ?"
]
MATCHING_PATTERNS = {
    "nom": "le nom du bénéficiaire",
    "prenom": "le prénom du bénéficiaire",
    "typeBeneficiaire": "le type de bénéficiaire 'Physique' ou 'Moral'",
    "rib": "le RIB (Relevé d'Identité Bancaire) du bénéficiaire",
    "newRib": "le nouveau RIB (Relevé d'Identité Bancaire) du bénéficiaire",
    "typeCompte": "le type de compte, comme 'Courant', 'Épargne', etc",
    "numeroFacture": "le numéro de facture associé à la transaction",
    "typeCarte": "le type de carte bancaire, par exemple 'Ambition', 'MasterCard MOURIH', etc.",
    "services": "les services associés à la carte , tels que 'Assurance voyage', 'Paiement en ligne' ",
    "raisonsOpposition": "les raisons pour lesquelles vous souhaitez opposer votre carte bancaire",
    "plafond": "le plafond autorisé",
    "typePlafond": "le type de plafond, par exemple 'Retraits, Paiement en ligne,..",
    "montant": "le montant de la transaction",
    "typeOperation": "le type du virement : instantané ou permanent"
}
PATTERNS = {
    "typeCompte": r"\bcompte\s+(principal|courant|habituel|chèques|à\svue|personnel|individuel|dépôt|virtuel|épargne"
                  r"|d'entreprise|entreprise|professionnel|placement)",
    "numeroCompte": r"\b0\s*0\s*0\s*\d\s*\d\s*\d\s*[a-zA-Z]\s*\d\s*\d\s*\d\s*\d\s*\d\s*\d\s*\d\s*\d\b",#\b[0]{3}[0-9]{3}[a-zA-Z][0-9]{9}\b
    "typeCarte": r"\b(MasterCard\s+(?:MOURIH|CLUB|WAJDA\s+GENERIQUE|WAJDA\s+BILA\s+HOUDOUD|LBANKALIK|FAYDA|WORLD\s"
                 r"+ELITE|CARTE\s+18-25)|VISA\s+ELECTRON\s+(?:AISANCE|KAFYA|WEB\s+PAY)|VISA\s+("
                 r"?:VISA\s+PREMIER|ESPACE|AMBITION|BILA\s+HOUDOUD|WORLD\s+ACCESS|RASMALI\s+CONFORT|RASMALI\s+AUTO\s"
                 r"+ENTRPRENEUR|BUSINESS\s+LOCALE|GOLD\s+INTERNATIONAL|SAHLA|POU\s+L|CORPORATE|PLATINIUL\s"
                 r"+INTERNATIONALE|ANA\s+MAAK\s+HIRAFI))",
    "numeroCarte": r"\b\d(?:\s?\d){15}\b",#^\d{4}(?:\s?\d{4}){3}$
    "statutCarte": r"\b(?:active|bloquée|expirée|annulée|en cours de fabrication|en attente de réception|opposée)",
    "montant": r"\b(\d+)\s*(?=(?:dirhams?|dh|MAD)\b)",
    "categorieOperation": r"\b(?:retrait|dépôt|virement|paiement|prélèvement)",
    "motif": r"\b(?:achat|salaire|loyer|facture|remboursement|dépense|alimentation|logement"
             r"|transport|divertissement|santé|épargne|impôts)",
    "demande_dateExpiration": r"(date d'expiration|expiration)",
    "demande_cvv": r"(Card Verification Value |cvv|CVV)",
    "demande_statutCarte": r"statut",
    "demande_codePin": r"(code pin|pin|code)",
    "rib": r"\b(\d{24}|\d{4}(?:\s?\d{4}){5}|(?:\s?\d){24})\b",#r"\b(\d{24}|\d{4}(?: \d{4}){5})\b",
    "typeBeneficiaire": r"\b(physique|morale)",
    "numeroFacture": r"\b(?:I\s*N\s*V)(?:\s*[A-Za-z0-9-_]){12,15}\b", #\b(?:I\s*N\s*V|F\s*A\s*C\s*T|B\s*L|P\s*O)(?:\s*[A-Za-z0-9-_]){12,19}\b",#\b(?:INV|FACT|BL|PO)[A-Za-z0-9-_]{12,19}\b
    "services": r"\b(?:paiement\s+(?:en\s+ligne|sans\s+contact|à\s+l'étranger)|retrait\s+d'espèces|tous\s+les\s"
                r"+services|assurances\s+(?:voyage|assistance)|conciergerie|rachat\s+("
                r"?:frais|franchise)|protections\s+(?:fraudeux|achats)|remise\s+(?:immédiate|différée)|privilèges\s+("
                r"?:exclusifs|voyage)|offres\s+(?:spéciales|partenaires)|accès\s+(?:aéroports|salons)|services\s+("
                r"?:bancaires|mobiles))\b",
    "raisonsOpposition": r"(fraude|utilisation\snon\sautorisée|transaction\ssuspecte|hameçonnage|escroquerie|vol"
                         r"|perte)",
    "typePlafond": r"(retraits|paiements|achats|dépenses|transferts|transactions|cashback|avance|crédit|débit)",
    "typeOperation": r"(instantané|permanent)",
}

INTENT_ACTIONS = {
    "Consultation_Solde": {
        "action": "consultation_solde_action",
        "patterns": extract_info(PATTERNS, ['typeCompte', 'numeroCompte'])
    },
    "Consultation_Opérations": {
        "action": "consultation_operations_action",
        "patterns": extract_info(PATTERNS, ['categorieOperation', 'montant', 'motif'])
    },
    "Consultation_Cartes": {
        "action": "consultation_cartes_action",
        "patterns": extract_info(PATTERNS,
                                 ['typeCarte', 'numeroCarte', 'demande_dateExpiration', 'demande_cvv',
                                  'demande_statutCarte', 'demande_codePin'])
    },
    "Gestion_Bénéficiare_Ajout": {
        "action": "action_ajout_beneficiaire",
        "complete_action": "action_complete_ajout_beneficiaire",
        "patterns": extract_info(PATTERNS, ['rib', "typeBeneficiaire"])
    },
    "Gestion_Bénéficiare_Suppression": {
        "action": "action_suppression_beneficiaire",
        "complete_action": "action_complete_suppression_beneficiaire",
        "patterns": extract_info(PATTERNS, ['rib'])
    },
    "Gestion_Bénéficiare_Modification": {
        "action": "action_modification_beneficiaire",
        "complete_action": "action_complete_modification_beneficiaire",
        "patterns": extract_info(PATTERNS, ['rib'])
    },
    "Info_Assistance": {
        "action": "info_assistance_action"
    },
    "Info_FAQ": {
        "action": "info_faq_action"
    },
    "Info_Geolocalisation": {
        "action": "info_geolocalisation_action"
    },
    "Transaction_Virement": {
        "action": "action_ajout_transaction_virement",
        "complete_action": "action_complete_ajout_transaction_virement",
        "patterns": extract_info(PATTERNS, ['typeCompte', 'rib', 'numeroCompte', 'montant', 'motif', 'typeOperation'])
    },
    "Transaction_PaiementFacture": {
        "action": "action_ajout_transaction_paiement",
        "complete_action": "action_complete_ajout_transaction_paiement",
        "patterns": extract_info(PATTERNS, ['typeCompte', 'numeroCompte', 'numeroFacture'])
    },
    "Gestion_Cartes_Activation": {
        "action": "action_activation_carte",
        "complete_action": "action_complete_activation_carte",
        "patterns": extract_info(PATTERNS, ['typeCarte', 'numeroCarte', 'services'])
    },
    "Gestion_Cartes_Désactivation": {
        "action": "action_desactivation_carte",
        "complete_action": "action_complete_desactivation_carte",
        "patterns": extract_info(PATTERNS, ['typeCarte', 'numeroCarte', 'services'])
    },
    "Gestion_Cartes_Opposition": {
        "action": "action_opposition_carte",
        "complete_action": "action_complete_opposition_carte",
        "patterns": extract_info(PATTERNS, ['typeCarte', 'numeroCarte', 'raisonsOpposition'])
    },
    "Gestion_Cartes_Augmenter_Plafond": {
        "action": "action_plafond_augmenter",
        "complete_action": "action_complete_plafond_augmenter",
        "patterns": extract_info(PATTERNS, ['numeroCarte', 'typeCarte', 'montant', 'typePlafond'])
    },
    "Gestion_Cartes_Diminuer_Plafond": {
        "action": "action_plafond_diminuer",
        "complete_action": "action_complete_plafond_diminuer",
        "patterns": extract_info(PATTERNS, ['numeroCarte', 'typeCarte', 'montant', 'typePlafond'])
    }
}
