import unittest
from unittest.mock import patch, MagicMock

import torch
import numpy as np
from transformers import AutoTokenizer, AutoModelForTokenClassification

from chatbot_api.utils.contstants import LABEL_ENCODER, MODEL, TOKENIZER
from chatbot_api.utils.nlu import intent_prediction, intent_recognition, entity_extraction


class TestIntentRecognition(unittest.TestCase):

    def setUp(self):
        # Create a mock tokenizer and model
        self.tokenizer = TOKENIZER
        self.model = MODEL
        self.loaded_encoder = LABEL_ENCODER

    def test_intent_prediction(self):
        text = "Je veux consulter ma carte bancaire"
        input_ids = torch.tensor([[1, 2, 3, 4, 5]])
        attention_mask = torch.ones_like(input_ids)

        # Mocking model forward pass
        with patch.object(self.model, 'forward') as mock_forward:
            mock_forward.return_value = MagicMock(logits=torch.tensor([[0.1, 0.2, 0.3]]))

            top_k_indices, top_k_probs = intent_prediction(self.model, self.tokenizer, text)

        mock_forward.assert_called_once_with(input_ids, attention_mask=attention_mask[:, :5])  # Adjusted size

        # Asserting the results
        self.assertIsInstance(top_k_indices, torch.Tensor)
        self.assertIsInstance(top_k_probs, np.ndarray)

    def test_intent_recognition_fallback(self):
        # Test when confidence below threshold
        text = "test text"
        with patch("chatbot_api.utils.nlu.intent_prediction", return_value=([1, 2, 3], [0.1, 0.2, 0.3])):
            intent = intent_recognition(text, self.model, self.tokenizer, MagicMock(), confidence_threshold=0.4)

        self.assertEqual(intent, "fallback")

    def test_intent_recognition(self):
        # Test when confidence above threshold
        text = "Je veux consulter ma carte bancaire"
        loaded_encoder = MagicMock()
        with patch("chatbot_api.utils.nlu.intent_prediction", return_value=([1, 2, 3], [0.8, 0.1, 0.05])):
            intent = intent_recognition(text, self.model, self.tokenizer, self.loaded_encoder, confidence_threshold=0.5)

        self.loaded_encoder.inverse_transform.assert_called_once_with([1])  # Adjusted assertion
        self.assertEqual(intent, self.loaded_encoder.inverse_transform.return_value[0])

    def test_entity_extraction(self):
        text = "test text"
        patterns = {"pattern1": r"\d{4}"}
        entities_ner_bert, entities_ner_regex = entity_extraction(text, patterns)

        # Asserting the results
        self.assertIsInstance(entities_ner_bert, list)
        self.assertIsInstance(entities_ner_regex, dict)


if __name__ == "__main__":
    unittest.main()
