import unittest
from unittest.mock import Mock

from chatbot_api.actions.IntentActions import IntentActions


class TestIntentActions(unittest.TestCase):

    def setUp(self):
        self.spring_api_service = Mock()
        self.intent_actions = IntentActions(self.spring_api_service)
        self.patterns = {}

    def test_info_assistance_action(self):
        response = self.intent_actions.info_assistance_action()
        self.assertTrue(response in self.intent_actions.intent_utils.ASSISTANCE_ACTION)

    def test_info_faq_action(self):
        data = {'text': 'Question fréquente'}
        expected_response = "Réponse à la question fréquente"
        self.spring_api_service.get_qa_chain.return_value = lambda x: {"result": expected_response}
        response = self.intent_actions.info_faq_action(data)
        self.assertEqual(response, expected_response)

    def test_info_geolocalisation_action(self):
        json_data = {'location': {'lat': 48.8566, 'lng': 2.3522}}
        expected_response = "Informations sur les agences à proximité"
        self.spring_api_service.get_data.return_value = expected_response
        response = self.intent_actions.info_geolocalisation_action(json_data)
        self.assertEqual(response, expected_response)

    def test_consultation_solde_action(self):
        data = {'text': 'solde de mon compte courant', 'userId': 123}
        expected_response = "Informations sur le solde du compte courant"
        self.spring_api_service.get_data.return_value = expected_response
        response = self.intent_actions.consultation_solde_action(data, self.patterns)
        self.assertEqual(response, expected_response)

    def test_consultation_cartes_action(self):
        data = {'text': 'cartes', 'userId': 123}
        expected_response = "Informations sur les cartes"
        self.spring_api_service.get_data.return_value = expected_response
        response = self.intent_actions.consultation_cartes_action(data, self.patterns)
        self.assertEqual(response, expected_response)

    def test_consultation_operations_action(self):
        data = {'text': 'opérations', 'userId': 123}
        expected_response = "Informations sur les opérations"
        self.spring_api_service.get_data.return_value = expected_response
        response = self.intent_actions.consultation_operations_action(data, self.patterns)
        self.assertEqual(response, expected_response)

    def test_action_ajout_beneficiaire(self):
        data = {'text': 'Ajoute un bénéficiaire Amine Bakkali à ma liste de bénéficiaires', 'userId': 123}
        expected_message = "Désolé, vous n'avez pas spécifié : le type de bénéficiaire 'Physique' ou 'Moral', le nom du bénéficiaire, le RIB (Relevé d'Identité Bancaire) du bénéficiaire. Veuillez les spécifier."
        response = self.intent_actions.action_ajout_beneficiaire(data, self.patterns)
        self.assertEqual(response[1], expected_message)

    def test_action_suppression_beneficiaire(self):
        data = {'text': 'Supprime le bénéficiaire John Doe', 'userId': 123}
        expected_message = "Vous voulez supprimer le bénéficiaire Doe John. Merci de confirmer cette demande!"
        response = self.intent_actions.action_suppression_beneficiaire(data, self.patterns)
        self.assertEqual(response[1], expected_message)

    def test_action_modification_beneficiaire(self):
        data = {'text': 'Modifie le bénéficiaire John Doe avec le nouveau RIB 123456789', 'userId': 123}
        expected_message = "Vous voulez modifier le bénéficiaire Doe John avec le nouveau RIB 123456789. Merci de confirmer cette demande!"
        response = self.intent_actions.action_modification_beneficiaire(data, self.patterns)
        self.assertEqual(response[1], expected_message)

    def test_action_activation_carte(self):
        data = {'text': 'Active les services paiement en ligne pour ma carte Visa', 'userId': 123}
        expected_message = "Désolé, vous n'avez pas spécifié : le type de carte bancaire, par exemple 'Ambition', 'MasterCard MOURIH', etc., les services associés à la carte , tels que 'Assurance voyage', 'Paiement en ligne' . Veuillez les spécifier."
        response = self.intent_actions.action_activation_carte(data, self.patterns)
        self.assertEqual(response[1], expected_message)

    def test_action_opposition_carte(self):
        data = {'text': 'Oppose ma carte Visa pour perte', 'userId': 123}
        expected_message = "Vous voulez opposer votre carte Visa pour perte. Merci de confirmer cette demande!"
        response = self.intent_actions.action_opposition_carte(data, self.patterns)
        self.assertEqual(response[1], expected_message)

    def test_action_plafond_augmenter(self):
        data = {'text': 'Augmente le plafond de retrait pour ma carte Visa', 'userId': 123}
        expected_message = "Vous voulez augmenter le plafond de retrait pour votre carte Visa. Merci de confirmer cette demande!"
        response = self.intent_actions.action_plafond_augmenter(data, self.patterns)
        self.assertEqual(response[1], expected_message)

    def test_action_plafond_diminuer(self):
        data = {'text': 'Diminue le plafond de paiement en ligne pour ma carte Visa', 'userId': 123}
        expected_message = "Vous voulez diminuer le plafond de paiement en ligne pour votre carte Visa. Merci de confirmer cette demande!"
        response = self.intent_actions.action_plafond_diminuer(data, self.patterns)
        self.assertEqual(response[1], expected_message)

    def test_action_ajout_transaction_virement(self):
        data = {'text': 'Vire 100 euros du compte courant au compte épargne', 'userId': 123}
        expected_message = "Entity_Missing_Transaction_Virement"
        self.spring_api_service.valid_account_num_rib.return_value = (True, expected_message)
        response = self.intent_actions.action_ajout_transaction_virement(data, self.patterns)
        self.assertEqual(response, expected_message)

    def test_action_ajout_transaction_paiement(self):
        data = {'text': 'Paye la facture 1234567 depuis mon compte courant', 'userId': 123}
        expected_intent = "Entity_Missing_Transaction_PaiementFacture"
        self.spring_api_service.valid_account_num_invoice.return_value = (True, expected_intent)
        response = self.intent_actions.action_ajout_transaction_paiement(data, self.patterns)
        self.assertEqual(response[0], expected_intent)

if __name__ == '__main__':
    unittest.main()
