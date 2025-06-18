import unittest
from unittest.mock import MagicMock, patch

# Import the functions to be tested
from chatbot_api.utils.bot import (
    handle_first_request,
    handle_security_question,
    handle_request,
    retrieve_action,
    handle_missing_entity,
    handle_request_validation
)
# Import necessary constants or functions
from chatbot_api.utils.contstants import LOGIN_SUCCESS_RESPONSES, FALLBACK_RESPONSES, LOGIN_FAIL_RESPONSES,INTENT_ACTIONS


class BotTest(unittest.TestCase):

    def setUp(self):
        # Create stubs or mocks where necessary
        self.spring_api_stub = MagicMock()
        self.model_stub = MagicMock()
        self.tokenizer_stub = MagicMock()
        self.label_encoder_stub = MagicMock()
        self.security_validator_stub = MagicMock()

    def test_handle_first_request(self):
        # Mocking random.choice to return a predictable value
        with patch('random.choice', return_value="What's your name?"):
            result = handle_first_request()
        self.assertEqual(result[0], "security_question")
        self.assertIsNotNone(result[1])
        self.assertEqual(result[2], "What's your name?")

    def test_handle_security_question_correct(self):
        # Stubbing the function to return a predefined response
        self.security_validator_stub.return_value = MagicMock(return_value="1985-08-20")
        # Patching the SecurityValidator class to return the mock instance
        with patch('chatbot_api.SecurityValidator', return_value=self.security_validator_stub):
            result = handle_security_question('Quelle est votre date de naissance ?',
                                              {"text": "1985-08-20", "userId": 1})
        # Check if the security question is correct
        if result[0] == "user_request":
            self.assertIn(result[1], LOGIN_SUCCESS_RESPONSES)
        else:
            self.assertIsNone(result[0])  # Expecting None if security question is incorrect
            self.assertIn(result[1], LOGIN_FAIL_RESPONSES)

    def test_handle_security_question_incorrect(self):
        # Stubbing the function to return a different response
        self.security_validator_stub.get_data.return_value = {"response": "1985-08-15"}
        # Mocking the dynamic function call
        self.security_validator_stub.return_value = MagicMock(return_value="1985-08-15")
        # Patching the SecurityValidator class to return the mock instance
        with patch('chatbot_api.SecurityValidator', return_value=self.security_validator_stub):
            result = handle_security_question('Quelle est votre date de naissance ?',
                                              {"text": "1985-08-20", "userId": "testuserid"})
        self.assertEqual(result[0],None)
        self.assertIn(result[1], LOGIN_FAIL_RESPONSES)

    def test_handle_request_fallback(self):
        # Stubbing intent_recognition to return 'fallback'
        with patch('chatbot_api.utils.bot.intent_recognition', return_value="fallback"):
            result = handle_request({"text": "invalid input"}, self.model_stub, self.tokenizer_stub,
                                    self.label_encoder_stub)
        self.assertEqual(result[0], "user_request")
        self.assertIsNotNone(result[1])

    def test_handle_request_retrieval(self):
        # Stubbing intent_recognition to return a known intent
        with patch('chatbot_api.utils.bot.intent_recognition', return_value="some_known_intent"):
            # Stubbing retrieve_action to return a predefined response
            with patch('chatbot_api.utils.bot.retrieve_action',
                       return_value=("some_known_intent", "some_response", {})):
                result = handle_request({"text": "valid input"}, self.model_stub, self.tokenizer_stub,
                                        self.label_encoder_stub)
        self.assertEqual(result[0], "some_known_intent")
        self.assertEqual(result[1], "some_response")

    def test_retrieve_action(self):
        # Stubbing INTENT_ACTIONS and action function to return a predefined response
        INTENT_ACTIONS = {
            "some_intent": {"action": "some_action"},
        }
        data = {"text": "some input"}
        extracted_entities = None

        # Mocking the action function call
        action_function_name = INTENT_ACTIONS["some_intent"]["action"]
        action_function_mock = MagicMock(return_value="some_response")
        intent_actions_instance_mock = MagicMock()
        setattr(intent_actions_instance_mock, action_function_name, action_function_mock)

        with patch('chatbot_api.utils.bot.IntentActions', return_value=intent_actions_instance_mock), \
                patch('chatbot_api.utils.bot.INTENT_ACTIONS', INTENT_ACTIONS):
            result = retrieve_action(data, "some_intent", extracted_entities)

        action_function_mock.assert_called_once_with(data)
        self.assertEqual(result[0], "some_intent")
        self.assertEqual(result[1], "some_response")
        self.assertIsNone(result[2])

    def test_handle_missing_entity(self):
        # Stubbing intent_recognition to return a known intent
        with patch('chatbot_api.utils.bot.intent_recognition', return_value="missing_entity_intent"):
            # Stubbing handle_missing_entity to return a predefined response
            with patch('chatbot_api.utils.bot.handle_missing_entity',
                       return_value=("user_request", "some_response", {})):
                result = handle_missing_entity({"text": "invalid text"}, "Entity_Missing_some_intent", {},
                                               self.model_stub, self.tokenizer_stub, self.label_encoder_stub)
        self.assertEqual(result[0], "user_request")
        self.assertIn(result[1], FALLBACK_RESPONSES)
        self.assertEqual(result[2], {})

    def test_handle_request_validation(self):
        # Stubbing intent_recognition to return a known intent
        with patch('chatbot_api.utils.bot.intent_recognition', return_value="request_validation_intent"):
            # Stubbing handle_request_validation to return a predefined response
            with patch('chatbot_api.utils.bot.handle_request_validation',
                       return_value=("user_request", "some_response", {})):
                result = handle_request_validation({"text": "Oui, je confirme"}, "Request_Validation_some_intent",
                                                   self.model_stub, self.tokenizer_stub, self.label_encoder_stub, {})
        self.assertEqual(result[0], "user_request")
        self.assertIsInstance(result[1], str)
        self.assertEqual(result[2], {})


if __name__ == "__main__":
    unittest.main()
