import unittest
from unittest.mock import Mock

from chatbot_api.actions.managers.card_manager import CardManager


class TestCardManager(unittest.TestCase):

    def setUp(self):
        self.spring_api = Mock()
        self.card_manager = CardManager(self.spring_api)

    def test_update_card_services_by_type(self):
        user_id = '123'
        extracted_entities = {'typeCarte': 'debit', 'services': ['online_banking', 'contactless'], 'status': 'Activation'}
        expected_response = {"status": "success", "message": "Card services updated successfully"}
        self.spring_api.put_data.return_value = expected_response
        response = self.card_manager.update_card_services_by_type(user_id, extracted_entities, 'Activation')
        self.assertEqual(response, expected_response)

    def test_update_card_services_by_number(self):
        user_id = '123'
        extracted_entities = {'typeCarte': 'debit', 'numeroCarte': '123456789', 'services': ['online_banking', 'contactless'], 'status': 'Activation'}
        expected_response = {"status": "success", "message": "Card services updated successfully"}
        self.spring_api.put_data.return_value = expected_response
        response = self.card_manager.update_card_services_by_number(user_id, extracted_entities, 'Activation')
        self.assertEqual(response, expected_response)

    def test_oppose_card_by_type(self):
        user_id = '123'
        extracted_entities = {'typeCarte': 'debit', 'raisonsOpposition': ['lost_card']}
        expected_response = {"status": "success", "message": "Card opposed successfully"}
        self.spring_api.put_data.return_value = expected_response
        response = self.card_manager.oppose_card_by_type(user_id, extracted_entities)
        self.assertEqual(response, expected_response)

    def test_oppose_card_by_num(self):
        user_id = '123'
        extracted_entities = {'typeCarte': 'debit', 'numeroCarte': '123456789', 'raisonsOpposition': ['lost_card']}
        expected_response = {"status": "success", "message": "Card opposed successfully"}
        self.spring_api.put_data.return_value = expected_response
        response = self.card_manager.oppose_card_by_num(user_id, extracted_entities)
        self.assertEqual(response, expected_response)

    def test_update_card_limit_by_type(self):
        user_id = '123'
        extracted_entities = {'typeCarte': 'debit', 'typePlafond': 'daily_limit', 'plafond': 2000, 'status': 'Augmenter'}
        expected_response = {"status": "success", "message": "Card limit updated successfully"}
        self.spring_api.put_data.return_value = expected_response
        response = self.card_manager.update_card_limit_by_type(user_id, extracted_entities, 'Augmenter')
        self.assertEqual(response, expected_response)

    def test_update_card_limit_by_num(self):
        user_id = '123'
        extracted_entities = {'typeCarte': 'debit', 'numeroCarte': '123456789', 'typePlafond': 'daily_limit', 'plafond': 2000, 'status': 'Augmenter'}
        expected_response = {"status": "success", "message": "Card limit updated successfully"}
        self.spring_api.put_data.return_value = expected_response
        response = self.card_manager.update_card_limit_by_num(user_id, extracted_entities, 'Augmenter')
        self.assertEqual(response, expected_response)

if __name__ == '__main__':
    unittest.main()
