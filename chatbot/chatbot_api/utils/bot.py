import random

from chatbot_api.actions.CompleteIntentActions import CompleteIntentActions
from chatbot_api.actions.IntentActions import IntentActions
from chatbot_api.utils.SecurityValidator import SecurityValidator
from chatbot_api.utils.SpringAPIService import SpringAPIService
from chatbot_api.utils.contstants import GREET_RESPONSES, QUESTION_TO_FUNCTION, INTENT_ACTIONS, SPRING_BASE_URL, \
    FALLBACK_RESPONSES, USERNAME, PASSWORD, ROLE, LOGIN_SUCCESS_RESPONSES, LOGIN_FAIL_RESPONSES
from chatbot_api.utils.nlu import intent_recognition

spring_api_service = SpringAPIService(SPRING_BASE_URL, USERNAME, PASSWORD, ROLE)


def handle_first_request():
    # Select a random security question
    security_question = random.choice(list(QUESTION_TO_FUNCTION.keys()))
    return "security_question", random.choice(GREET_RESPONSES), security_question


def handle_security_question(security_question, data):
    text = data['text'].lower()
    userid = data['userId']
    function_name = QUESTION_TO_FUNCTION[security_question]
    # Instantiate SecurityValidator
    security_validator = SecurityValidator(spring_api_service, userid)
    # Call the method dynamically using getattr
    function = getattr(security_validator, function_name)
    if str(function()).lower() == text:
        return "user_request", random.choice(LOGIN_SUCCESS_RESPONSES)
    else:
        return None, random.choice(LOGIN_FAIL_RESPONSES)


def handle_request(data, model, tokenizer, label_encoder):
    extracted_entities = None
    text = data['text'].lower()
    intent = intent_recognition(text, model, tokenizer, label_encoder)

    if intent == "fallback":
        return "user_request", random.choice(FALLBACK_RESPONSES)
    else:
        return retrieve_action(data, intent, extracted_entities)


def retrieve_action(data, intent, extracted_entities):
    # Retrieve action function from the intent dictionnary
    action_function_name = INTENT_ACTIONS[intent]["action"]
    patterns = INTENT_ACTIONS[intent].get("patterns", None)
    intent_actions = IntentActions(spring_api_service)
    action_function = getattr(intent_actions, action_function_name)

    if patterns is not None:
        response = action_function(data, patterns)
        if len(response) == 2:
            intent, response = response
        elif len(response) == 3:
            intent, response, extracted_entities = response
        return intent, response, extracted_entities
    else:
        if intent != "Info_Assistance":
            response = action_function(data)
        else:
            response = action_function()

    return intent, response


def handle_missing_entity(data, context, extracted_entities, model, tokenizer, label_encoder):
    text = data['text'].lower()
    intent = context.split("Entity_Missing_")[1]
    switch_intent = intent_recognition(text, model, tokenizer, label_encoder)
    print(intent)
    print(switch_intent)
    if intent in INTENT_ACTIONS:
        if switch_intent not in [intent, "fallback", "Confirmation_Action", "Annulation_Action"]:
            extracted_entities = {}
            return retrieve_action(data, switch_intent, extracted_entities)
        else:
            intent_actions = CompleteIntentActions(spring_api_service)
            patterns = INTENT_ACTIONS[intent].get("patterns", None)
            action_function_name = INTENT_ACTIONS[intent]['complete_action']
            action_function = getattr(intent_actions, action_function_name)
            return action_function(data, extracted_entities, patterns, "Missing_Entity")
    else:
        return "user_request", random.choice(FALLBACK_RESPONSES), {}


def handle_request_validation(data, context, model, tokenizer, label_encoder, extracted_entities):
    previous_intent = context.split("Request_Validation_")[1]
    text = data['text'].lower()
    intent = intent_recognition(text, model, tokenizer, label_encoder)
    if intent == "Confirmation_Action" or intent == "Annulation_Action":
        if previous_intent in INTENT_ACTIONS:
            intent_actions = CompleteIntentActions(spring_api_service)
            action_function_name = INTENT_ACTIONS[previous_intent]['complete_action']
            patterns = INTENT_ACTIONS[previous_intent].get("patterns", None)
            action_function = getattr(intent_actions, action_function_name)
            return action_function(data, extracted_entities, patterns, intent)
    else:
        return "user_request", random.choice(FALLBACK_RESPONSES), {}
