import unittest
from unittest.mock import MagicMock

from chatbot_api.utils.SpringAPIService import SpringAPIService


class TestSpringAPIService(unittest.TestCase):

    def setUp(self):
        # Create a stub for requests
        self.requests_stub = MagicMock()

        # Create a SpringAPIService instance with the stub
        self.spring_api_service = SpringAPIService(
            base_url="http://example.com",
            username="testuser",
            password="testpassword",
            role="testrole",
        )
        # Replace requests.post with the stub
        self.spring_api_service._make_request = self.requests_stub

    def test_get_data(self):
        # Set up the stub to return predefined data when called
        self.requests_stub.return_value = {"key": "value"}

        # Call the method under test
        result = self.spring_api_service.get_data("test_endpoint")

        # Assert the result
        self.assertEqual(result, {"key": "value"})
        self.requests_stub.assert_called_once_with('get', 'test_endpoint')

    def test_post_data_check(self):
        # Set up the stub to return predefined data when called
        self.requests_stub.return_value = {"status": "success"}

        # Call the method under test
        result = self.spring_api_service.post_data_check("test_endpoint", {"data": "test_data"})

        # Assert the result
        self.assertEqual(result, {"status": "success"})
        self.requests_stub.assert_called_once_with('post', 'test_endpoint', {"data": "test_data"})

    def test_post_data_text(self):
        # Set up the stub to return predefined data when called
        self.requests_stub.return_value = "Success message"

        # Call the method under test
        result = self.spring_api_service.post_data_text("test_endpoint", {"data": "test_data"})

        # Assert the result
        self.assertEqual(result, "Success message")
        self.requests_stub.assert_called_once_with('post', 'test_endpoint', {"data": "test_data"},
                                                   success_status={200, 201}, error_status={400, 404})

    def test_delete_data(self):
        # Set up the stub to return predefined data when called
        self.requests_stub.return_value = {"status": "deleted"}

        # Call the method under test
        result = self.spring_api_service.delete_data("test_endpoint", {"data": "test_data"})

        # Assert the result
        self.assertEqual(result, {"status": "deleted"})
        self.requests_stub.assert_called_once_with('delete', 'test_endpoint', {"data": "test_data"},
                                                   success_status={200, 204}, error_status={404, 400})

    def test_put_data(self):
        # Set up the stub to return predefined data when called
        self.requests_stub.return_value = {"status": "updated"}

        # Call the method under test
        result = self.spring_api_service.put_data("test_endpoint", {"data": "test_data"})

        # Assert the result
        self.assertEqual(result, {"status": "updated"})
        self.requests_stub.assert_called_once_with('put', 'test_endpoint', {"data": "test_data"},
                                                   success_status={200, 201}, error_status={400, 404})


if __name__ == "__main__":
    unittest.main()
