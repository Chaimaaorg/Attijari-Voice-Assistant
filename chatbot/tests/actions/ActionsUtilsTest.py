import unittest
from unittest.mock import Mock

from chatbot_api.actions.ActionsUtils import ActionsUtils
from chatbot_api.utils.util_functions import get_missing_entity_message

class TestActionsUtils(unittest.TestCase):

    def setUp(self):
        self.spring_api_service = Mock()
        self.actions_utils = ActionsUtils(self.spring_api_service)
        self.MATCHING_PATTERNS = {
            'typeBeneficiaire': 'Type de bénéficiaire',
            'rib': 'Numéro de RIB',
            # Ajoutez d'autres motifs ici si nécessaire
        }

    def test_handle_missing_entity(self):
        missing_entities = ['typeBeneficiaire', 'rib']
        extracted_entities = {'typeBeneficiaire': 'personne', 'rib': '123456789'}
        context = 'Missing_Entity'
        expected_message = get_missing_entity_message(['Type de bénéficiaire', 'Numéro de RIB'])
        expected_result = ("Entity_Missing_Missing_Entity", expected_message, extracted_entities)
        result = self.actions_utils.handle_missing_entity(missing_entities, self.MATCHING_PATTERNS, extracted_entities, context)
        self.assertEqual(result, expected_result)

    def test_check_beneficiaires(self):
        userid = '123'
        extracted_entities = {'nom': 'Doe', 'prenom': 'John'}
        expected_response = ["Beneficiaire 1", "Beneficiaire 2"]
        self.spring_api_service.post_data_check.return_value = expected_response
        response = self.actions_utils.check_beneficiaires(userid, extracted_entities)
        self.assertEqual(response, expected_response)


if __name__ == '__main__':
    unittest.main()
