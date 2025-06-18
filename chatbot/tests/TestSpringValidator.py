import unittest
from unittest.mock import MagicMock
from chatbot_api.utils.SecurityValidator import SecurityValidator

class TestSpringValidator(unittest.TestCase):

    def setUp(self):
        # Create a stub for the spring_api_service
        self.spring_api_stub = MagicMock()

        # Create a SecurityValidator instance with the stub
        self.security_validator = SecurityValidator(self.spring_api_stub, userid="testuserid")

    def test_provide_date_of_birth(self):
        # Set up the stub to return predefined data when called
        self.spring_api_stub.get_data.return_value = {"client": {"dateNaissance": "1990-01-01"}}

        # Call the method under test
        result = self.security_validator.provide_date_of_birth()

        # Assert the result
        self.assertEqual(result, "1990-01-01")

    def test_provide_user(self):
        # Set up the stub to return predefined data when called
        self.spring_api_stub.get_data.return_value = {"userId": "testuserid"}

        # Call the method under test
        result = self.security_validator.provide_user()

        # Assert the result
        self.assertEqual(result, {"userId": "testuserid"})

    def test_provide_email_address(self):
        # Set up the stub to return predefined data when called
        self.spring_api_stub.get_data.return_value = {"email": "test@example.com"}

        # Call the method under test
        result = self.security_validator.provide_email_address()

        # Assert the result
        self.assertEqual(result, "test@example.com")

    def test_provide_postal_address(self):
        # Set up the stub to return predefined data when called
        self.spring_api_stub.get_data.return_value = {"client": {"adresse": "123 Main St"}}

        # Call the method under test
        result = self.security_validator.provide_postal_address()

        # Assert the result
        self.assertEqual(result, "123 Main St")

    def test_provide_phone_number(self):
        # Set up the stub to return predefined data when called
        self.spring_api_stub.get_data.return_value = {"telephone": "123-456-7890"}

        # Call the method under test
        result = self.security_validator.provide_phone_number()

        # Assert the result
        self.assertEqual(result, "123-456-7890")

    def test_provide_last_transaction_amount(self):
        # Set up the stub to return predefined data when called
        self.spring_api_stub.get_data.return_value = [
            {"dateOperation": "2024-05-01", "montant": 100},
            {"dateOperation": "2024-04-01", "montant": 200}
        ]

        # Call the method under test
        result = self.security_validator.provide_last_transaction_amount()

        # Assert the result
        self.assertEqual(result, 100)

    def test_provide_agency(self):
        # Set up the stub to return predefined data when called
        self.spring_api_stub.get_data.return_value = [{"nomAgence": "Main Street Branch"}]

        # Call the method under test
        result = self.security_validator.provide_agency()

        # Assert the result
        self.assertEqual(result, "Main Street Branch")

if __name__ == "__main__":
    unittest.main()
