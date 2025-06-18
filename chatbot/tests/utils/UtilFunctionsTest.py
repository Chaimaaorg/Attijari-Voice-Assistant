import unittest

from chatbot_api.utils.util_functions import *


class UtilFunctionsTest(unittest.TestCase):

    def setUp(self):
        self.data = {
            "dateOperation": "2024-05-01",
            "beneficiaire": {"nom": "Doe", "prenom": "John"},
            "montant": 100,
            "categorieOperation": "Dépôt",
            "motif": "Sans motif",
            "compte": {"typeCompte": "Épargne"}
        }

        self.entities = [
            {"entity_group": "DATE", "word": "01/05/2024"},
            {"entity_group": "PER", "word": "John Doe"}
        ]

        self.operations = [
            {
                "dateOperation": "2024-04-20",
                "beneficiaire": {"nom": "Doe", "prenom": "Jane"},
                "montant": 50,
                "categorieOperation": "Retrait",
                "motif": "Courses",
                "compte": {"typeCompte": "Courant", "numeroCompte": "123456789OI5484"}
            },
            self.data
        ]

        self.cartes = [
            {
                "typeCarte": "Visa",
                "dateExpiration": datetime(2025, 5, 1),
                "cvv": 123,
                "statutCarte": "active",
                "codePin": 456,
                "services": ["Paiement en ligne", "Retraits"],
                "dateOpposition": None,
                "raisonsOpposition": None
            }
        ]

        self.comptes = [
            {"typeCompte": "Courant", "solde": 1000, "numeroCompte": "1234I987465236"}]

        self.agences = [
            {
                "nomAgence": "Agence Centrale",
                "adresse": "123 Rue Principale",
                "horairesOuverture": "Lundi-Vendredi: 9h00-18h00",
                "servicesDisponibles": ["Retraits", "Virements", "Conseiller financier"],
                "telephone": "0123456789"
            }
        ]

    def test_extract_info(self):
        keys = ["dateOperation", "beneficiaire", "montant"]
        expected_output = {
            "dateOperation": "2024-05-01",
            "beneficiaire": {"nom": "Doe", "prenom": "John"},
            "montant": 100
        }
        self.assertEqual(extract_info(self.data, keys), expected_output)

    def test_convert_to_french_date(self):
        datetime_obj = datetime(2024, 5, 1)
        expected_output = "01/05/2024"
        self.assertEqual(convert_to_french_date(datetime_obj), expected_output)

    def test_extract_date_from_entity(self):
        expected_output = "01/05/2024"
        self.assertEqual(extract_date_from_entity("01/05/2024"), expected_output)

    def test_compare_dates(self):
        operation_date = "2024-05-01"
        date = "01/05/2024"
        self.assertTrue(compare_dates(operation_date, date))

    def test_filter_operations(self):
        entities = [
            {"entity_group": "DATE", "word": "01/05/2024"},
            {"entity_group": "PER", "word": "John Doe"}
        ]
        filtered_operations = filter_operations(self.operations, entities)
        self.assertIn(self.data, filtered_operations)

    def test_build_message_for_request(self):
        request_list = ["dateExpiration", "cvv", "statutCarte", "codePin"]
        carte = self.cartes[0]
        expected_output = ("Votre carte Visa va expirer le 01/05/2025. "
                           "Elle a pour code de sécurité 123. "
                           "La carte est actuellement active. "
                           "Elle a pour code pin 456.")
        self.assertEqual(build_message_for_request(request_list, carte), expected_output)

    def test_build_message_info_operation(self):
        expected_output = ("Vous avez effectué un : Dépôt avec votre compte Épargne le 01/05/2024 "
                           "avec le montant 100 dirhams au compte de Doe John")
        self.assertEqual(build_message_info_operation(self.data), expected_output)

    def test_get_cartes_message(self):
        request_list = ["dateExpiration", "cvv", "statutCarte", "codePin"]
        expected_output = (
            "Votre carte Visa va expirer le 01/05/2025. Elle a pour code de sécurité 123. La carte est actuellement "
            "active. Elle a pour code pin 456.")
        self.assertEqual(get_cartes_message(request_list, self.cartes), expected_output)

    def test_get_operations_message(self):
        filtered_operations = [self.data]
        expected_output = ("Voici les informations de vos opérations bancaires :"
                           "Vous avez effectué un : Dépôt avec votre compte Épargne le 01/05/2024 "
                           "avec le montant 100 dirhams au compte de Doe John")
        self.assertEqual(get_operations_message(self.operations, filtered_operations), expected_output)

    def test_get_comptes_message(self):
        expected_output = "Le solde disponible sur votre compte Courant est de 1000 dirhams."
        self.assertEqual(get_comptes_message(self.comptes), expected_output)

    def test_get_agences_messages(self):
        expected_output = (
            "Vous pouvez trouver notre agence Agence Centrale située au 123 Rue Principale. Elle est ouverte du Lundi "
            "jusqu'à Vendredi de  9h00 à 18h00 et propose les services suivants : ['Retraits', 'Virements', "
            "'Conseiller financier']. Vous pouvez également la contacter au 0123456789.")
        self.assertEqual(get_agences_messages(self.agences)[0], expected_output)

    def test_get_missing_entity_message(self):
        missing_entities = ["date d'expiration", "code de sécurité"]
        expected_output = "Désolé, vous n'avez pas spécifié : date d'expiration, code de sécurité. Veuillez les spécifier."
        self.assertEqual(get_missing_entity_message(missing_entities), expected_output)

    def test_merge_entities(self):
        extracted_entities = {"dateOperation": "2024-05-01"}
        new_extracted_entities = {"beneficiaire": "Doe John"}
        expected_output = {"dateOperation": "2024-05-01", "beneficiaire": "Doe John"}
        self.assertEqual(merge_entities(extracted_entities, new_extracted_entities), expected_output)

    def test_extract_names(self):
        expected_output = ("John", "Doe")
        self.assertEqual(extract_names(self.entities), expected_output)


if __name__ == '__main__':
    unittest.main()
