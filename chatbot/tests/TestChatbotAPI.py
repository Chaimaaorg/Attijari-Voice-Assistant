import time
from unittest.mock import patch

import pytest
from fastapi.testclient import TestClient

from chatbot_api.utils.contstants import *
from main import app

client = TestClient(app)

# Mock des contextes utilisateur
user_contexts = {}
user_security_questions = {}
user_extracted_entities = {}

async def mock_cleanup_user_data(interval_seconds):
    pass
@pytest.mark.asyncio
async def test_first_request():
    response = client.post('/', json={"userId": 1})
    assert response.status_code == 200
    assert "response" in response.json()

    response_text = response.json()["response"]
    greeting = response_text.split('.')[0]
    assert any(greeting in expected_response for expected_response in GREET_RESPONSES)

@patch("main.cleanup_user_data", side_effect=mock_cleanup_user_data)
async def test_missing_entity():
    # Définir le contexte actuel comme "user_request"
    user_contexts["123"] = {'timestamp': time.time(), 'context': "user_request"}
    user_security_questions["123"] = None
    user_extracted_entities["123"] = {}

    response = client.post('/', json={"userId": "123", "message": "Je veux ajouter un bénéficiaire"})
    assert response.status_code == 200
    assert "response" in response.json()
    assert "Désolé, vous n'avez pas spécifié" in response.json()["response"]

@pytest.mark.asyncio
async def test_request_validation():
    response =  client.post('/', json={"userId": 123, "message": "medium"})
    assert response.status_code == 200
    assert "response" in response.json()
    assert "Would you like any toppings?" in response.json()["response"]

@pytest.mark.asyncio
async def test_handle_request():
    response =  client.post('/', json={"userId": 123, "message": "yes"})
    assert response.status_code == 200
    assert "response" in response.json()
    assert "Your pizza will be delivered shortly!" in response.json()["response"]
