import unittest
from unittest.mock import Mock

from chatbot_api.actions.managers.beneficiary_manager import BeneficiaryManager


class TestBeneficiaryManager(unittest.TestCase):

    def setUp(self):
        self.api_service = Mock()
        self.beneficiary_manager = BeneficiaryManager(self.api_service)

    def test_add_beneficiary(self):
        user_id = '123'
        data = {'nom': 'Doe', 'prenom': 'John', 'rib': '123456789', 'typeBeneficiaire': 'personne'}
        expected_response = {"status": "success", "message": "Beneficiary added successfully"}
        self.api_service.post_data_text.return_value = expected_response
        response = self.beneficiary_manager.add_beneficiary(user_id, data)
        self.assertEqual(response, expected_response)

    def test_delete_beneficiary_by_name(self):
        user_id = '123'
        data = {'nom': 'Doe', 'prenom': 'John'}
        expected_response = {"status": "success", "message": "Beneficiary deleted successfully"}
        self.api_service.delete_data.return_value = expected_response
        response = self.beneficiary_manager.delete_beneficiary_by_name(user_id, data)
        self.assertEqual(response, expected_response)

    def test_delete_beneficiary_by_rib(self):
        user_id = '123'
        data = {'rib': '123456789'}
        expected_response = {"status": "success", "message": "Beneficiary deleted successfully"}
        self.api_service.delete_data.return_value = expected_response
        response = self.beneficiary_manager.delete_beneficiary_by_rib(user_id, data)
        self.assertEqual(response, expected_response)

    def test_get_beneficiaries(self):
        user_id = '123'
        rib = '123456789'
        expected_beneficiary = {"nom": "Doe", "prenom": "John", "rib": "123456789", "typeBeneficiaire": "personne"}
        self.api_service.post_data_check.return_value = expected_beneficiary
        response = self.beneficiary_manager.get_beneficiaries(user_id, rib)
        self.assertEqual(response, expected_beneficiary)

    def test_update_beneficiary_by_name(self):
        user_id = '123'
        extracted_entities = {'nom': 'Doe', 'prenom': 'John', 'newRib': '987654321'}
        expected_response = {"status": "success", "message": "Beneficiary updated successfully"}
        self.api_service.put_data.return_value = expected_response
        response = self.beneficiary_manager.update_beneficiary_by_name(user_id, extracted_entities)
        self.assertEqual(response, expected_response)

    def test_update_beneficiary_by_rib(self):
        user_id = '123'
        extracted_entities = {'rib': '123456789', 'newRib': '987654321'}
        expected_response = {"status": "success", "message": "Beneficiary updated successfully"}
        self.api_service.put_data.return_value = expected_response
        response = self.beneficiary_manager.update_beneficiary_by_rib(user_id, extracted_entities)
        self.assertEqual(response, expected_response)

if __name__ == '__main__':
    unittest.main()
