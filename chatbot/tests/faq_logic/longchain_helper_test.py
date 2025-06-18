import unittest
import os
from unittest.mock import patch

from chatbot_api.faq_logic.langchain_helper import create_vector_db, get_or_create_vector_db, get_qa_chain


class TestFunctions(unittest.TestCase):

    def test_create_vector_db(self):
        # Test if create_vector_db function successfully creates the vector database
        vector_db = create_vector_db()
        self.assertIsNotNone(vector_db)

    def test_get_or_create_vector_db_existing(self):
        # Test if get_or_create_vector_db function returns existing vector database
        with patch('os.path.isfile', return_value=True):
            vector_db = get_or_create_vector_db()
        self.assertIsNotNone(vector_db)

    def test_get_or_create_vector_db_new(self):
        # Test if get_or_create_vector_db function creates a new vector database
        with patch('os.path.isfile', return_value=False):
            vector_db = get_or_create_vector_db()
        self.assertIsNotNone(vector_db)

    def test_get_qa_chain(self):
        # Test if get_qa_chain function returns a valid QA chain
        chain = get_qa_chain()
        self.assertIsNotNone(chain)


if __name__ == "__main__":
    unittest.main()
