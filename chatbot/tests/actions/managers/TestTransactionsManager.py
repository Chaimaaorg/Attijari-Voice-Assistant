import unittest
from unittest.mock import Mock
from chatbot_api.actions.managers.transactions_manager import TransactionsManager

class TestTransactionsManager(unittest.TestCase):
    def test_add_transfer_by_account_type_names(self):
        spring_api_mock = Mock()
        transactions_manager = TransactionsManager(spring_api_mock)
        userid = "user123"
        extracted_entities = {
            'typeCompte': 'compteCourant',
            'nom': 'Dupont',
            'prenom': 'Jean',
            'motif': 'loyer',
            'montant': 1000,
            'typeOperation': 'virement'
        }
        expected_body = {
            "userId": userid,
            "compte": {
                "typeCompte": extracted_entities.get('typeCompte')
            },
            "beneficiaire": {
                "nom": extracted_entities.get('nom'),
                "prenom": extracted_entities.get('prenom')
            },
            "operation": {
                "motif": extracted_entities.get('motif', ""),
                "montant": extracted_entities.get('montant'),
                "typeOperation": extracted_entities.get('typeOperation')
            }
        }
        expected_endpoint = 'operations/addByAccountTypeBeneficiaryNames'
        transactions_manager.add_transfer_by_account_type_names(userid, extracted_entities)
        spring_api_mock.post_data_text.assert_called_once_with(expected_endpoint, expected_body)

    def test_add_transfer_by_account_type_rib(self):
        spring_api_mock = Mock()
        transactions_manager = TransactionsManager(spring_api_mock)
        userid = "user123"
        extracted_entities = {
            'typeCompte': 'compteCourant',
            'rib': '1234567890',
            'motif': 'loyer',
            'montant': 1000,
            'typeOperation': 'virement'
        }
        expected_body = {
            "userId": userid,
            "compte": {
                'typeCompte': extracted_entities.get('typeCompte')
            },
            "beneficiaire": {
                "rib": extracted_entities.get('rib')
            },
            "operation": {
                "motif": extracted_entities.get('motif', ""),
                "montant": extracted_entities.get('montant'),
                "typeOperation": extracted_entities.get('typeOperation')
            }
        }
        expected_endpoint = 'operations/addByAccountTypeAndRib'
        transactions_manager.add_transfer_by_account_type_rib(userid, extracted_entities)
        spring_api_mock.post_data_text.assert_called_once_with(expected_endpoint, expected_body)

    def test_add_transfer_by_account_num_rib(self):
        spring_api_mock = Mock()
        transactions_manager = TransactionsManager(spring_api_mock)
        userid = "user123"
        extracted_entities = {
            'numeroCompte': '9876543210',
            'rib': '1234567890',
            'motif': 'loyer',
            'montant': 1000,
            'typeOperation': 'virement'
        }
        expected_body = {
            "userId": userid,
            "compte": {
                "numeroCompte": extracted_entities.get('numeroCompte')
            },
            "beneficiaire": {
                "rib": extracted_entities.get('rib')
            },
            "operation": {
                "motif": extracted_entities.get('motif', ""),
                "montant": extracted_entities.get('montant'),
                "typeOperation": extracted_entities.get('typeOperation')
            }
        }
        expected_endpoint = 'operations/addByAccountNumAndRib'
        transactions_manager.add_transfer_by_account_num_rib(userid, extracted_entities)
        spring_api_mock.post_data_text.assert_called_once_with(expected_endpoint, expected_body)

    def test_add_transfer_by_account_num_names(self):
        spring_api_mock = Mock()
        transactions_manager = TransactionsManager(spring_api_mock)
        userid = "user123"
        extracted_entities = {
            'numeroCompte': '9876543210',
            'nom': 'Dupont',
            'prenom': 'Jean',
            'montant': 1000,
            'typeOperation': 'virement'
        }
        expected_body = {
            "userId": userid,
            "compte": {
                'numeroCompte': extracted_entities.get('numeroCompte'),
            },
            "beneficiaire": {
                "nom": extracted_entities.get('nom'),
                "prenom": extracted_entities.get('prenom'),
            },
            "operation": {
                'montant': extracted_entities.get('montant'),
                'typeOperation': extracted_entities.get('typeOperation')
            }
        }
        expected_endpoint = 'operations/addByAccountNumBeneficiaryNames'
        transactions_manager.add_transfer_by_account_num_names(userid, extracted_entities)
        spring_api_mock.post_data_text.assert_called_once_with(expected_endpoint, expected_body)

    def test_add_invoice_by_account_type(self):
        spring_api_mock = Mock()
        transactions_manager = TransactionsManager(spring_api_mock)
        userid = "user123"
        extracted_entities = {
            'numeroFacture': 'F123456',
            'typeCompte': 'compteCourant'
        }
        expected_body = {
            "userId": userid,
            "numeroFacture": extracted_entities.get('numeroFacture'),
            "typeCompte": extracted_entities.get('typeCompte')
        }
        expected_endpoint = 'paiement-facture/addInvoiceByAccountType'
        transactions_manager.add_invoice_by_account_type(userid, extracted_entities)
        spring_api_mock.post_data_text.assert_called_once_with(expected_endpoint, expected_body)

    def test_add_invoice_by_account_num(self):
        spring_api_mock = Mock()
        transactions_manager = TransactionsManager(spring_api_mock)
        userid = "user123"
        extracted_entities = {
            'numeroFacture': 'F123456',
            'numeroCompte': '9876543210',
            'typeCompte': 'compteCourant'
        }
        expected_body = {
            "userId": userid,
            "numeroFacture": extracted_entities.get('numeroFacture'),
            "numeroCompte": extracted_entities.get('numeroCompte'),
            "typeCompte": extracted_entities.get('typeCompte')
        }
        expected_endpoint = 'paiement-facture/addInvoiceByAccountNum'
        transactions_manager.add_invoice_by_account_num(userid, extracted_entities)
        spring_api_mock.post_data_text.assert_called_once_with(expected_endpoint, expected_body)

if __name__ == '__main__':
    unittest.main()
