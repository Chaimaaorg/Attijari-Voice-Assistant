import numpy as np
import torch
import re
from transformers import AutoTokenizer, AutoModelForTokenClassification, pipeline



def intent_prediction(model, tokenizer, text, max_len=128, top_k=3):
    # Encode les textes
    tokenized_texts = tokenizer.encode_plus(
        text,
        add_special_tokens=True,
        max_length=max_len,
        padding="max_length",
        truncation=True,
        return_tensors="pt"
    )

    input_ids = tokenized_texts["input_ids"]
    attention_mask = tokenized_texts["attention_mask"]

    with torch.no_grad():
        # Pass forward, compute logits predictions
        outputs = model(input_ids, attention_mask=attention_mask)
        logits = outputs.logits
        # Get the indices of the top 3 predictions
        top_k_indices = torch.topk(logits, k=3, dim=1).indices.flatten()
        # Get the probabilities of the top 3 predictions
        top_k_probs = torch.softmax(logits, dim=1)[0, top_k_indices].tolist()

    # Get the top k predictions and their probabilities
    return top_k_indices, top_k_probs


def intent_recognition(text, model, tokenizer, loaded_encoder, confidence_threshold=0.5,
                       relative_difference_threshold=0.1, top_k=3):
    # Get the top predictions and their probabilities
    top_k_classes, top_k_predictions = intent_prediction(model, tokenizer, text, top_k=top_k)

    # Get the probability of the top prediction
    top_probability = max(top_k_predictions)

    # Check if the top probability is below the confidence threshold
    if top_probability < confidence_threshold:
        return "fallback"
    else:
        # Check the difference between the top probability and the second top probability
        top_k_predictions.sort()
        second_top_probability = top_k_predictions[1]
        if top_probability - second_top_probability < relative_difference_threshold:
            return "fallback"
        else:
            intent = loaded_encoder.inverse_transform([top_k_classes[0]])[0]

            return intent


def entity_extraction(text, patterns):
    # Initialize the tokenizer and the Hugging Face NER model
    tokenizer = AutoTokenizer.from_pretrained("Jean-Baptiste/camembert-ner-with-dates")
    model = AutoModelForTokenClassification.from_pretrained("Jean-Baptiste/camembert-ner-with-dates")
    nlp = pipeline('ner', model=model, tokenizer=tokenizer, aggregation_strategy="simple")

    # Extract named entities with the Hugging Face NER model
    entities_ner_bert = nlp(text)

    # Initialize a dictionary to store the values extracted with the regex patterns
    entities_ner_regex = {}

    # For each regex pattern provided in the patterns dictionary
    for key, pattern in patterns.items():
        # Compile the regex pattern
        regex_pattern = re.compile(pattern, re.IGNORECASE)
        # Search for the pattern in the text and extract the first match if any
        entity_ner_regex = regex_pattern.findall(text)
        # Store the extracted value in the entities_ner_regex dictionary with the corresponding key
        entities_ner_regex[key] = entity_ner_regex if entity_ner_regex else None

    return entities_ner_bert, entities_ner_regex

