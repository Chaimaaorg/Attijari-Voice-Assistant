import random
from chatbot_api.actions.ActionsUtils import ActionsUtils
from chatbot_api.faq_logic.langchain_helper import get_qa_chain, create_vector_db
from chatbot_api.utils.contstants import ASSISTANCE_ACTION, MATCHING_PATTERNS
from chatbot_api.utils.nlu import entity_extraction
from chatbot_api.utils.util_functions import filter_operations, get_cartes_message, get_operations_message, \
    get_comptes_message, get_agences_messages, extract_names


class IntentActions:
    def __init__(self, spring_api_service):
        self.spring_api = spring_api_service
        self.intent_utils = ActionsUtils(spring_api_service)

    def info_assistance_action(self):
        return random.choice(ASSISTANCE_ACTION)

    def info_faq_action(self, data):
        text = data['text'].lower()
        chain = get_qa_chain()
        answer = chain(text)
        response = answer["result"]
        return response

    def info_geolocalisation_action(self, json_data):
        if "location" in json_data:
            lat = json_data['location']["lat"]
            lng = json_data['location']["lng"]
            agences = self.spring_api.get_data(f"agences/location/{lat}/{lng}")
        else:
            user_id = json_data["userId"]
            agences = self.spring_api.get_data(f"agences/userAddress/{user_id}")

        return get_agences_messages(agences)

    def consultation_solde_action(self, data, patterns):
        text = data['text'].lower()
        userid = data['userId']
        entities_dict = {}
        _, entities_ner_regex = entity_extraction(text, patterns)
        if entities_ner_regex:
            for patternName, value in entities_ner_regex.items():
                if value:
                    if patternName == "numeroCompte":
                        entities_dict[patternName] = value[0].replace(" ", "").upper()
                    else:
                        entities_dict[patternName] = value[0]
            response_body = {"userId": userid, "entitiesDic": entities_dict}
            print(response_body)
            comptes = self.spring_api.post_data_check('comptes/searchEntitiesDict', response_body)
            return get_comptes_message(comptes)
        else:
            comptes = self.spring_api.get_data(f"comptes/user/{userid}")
            return get_comptes_message(comptes)

    def consultation_cartes_action(self, data, patterns):
        text = data['text'].lower()
        userid = data['userId']
        entities_dict = {}
        request_list = []
        _, entities_ner_regex = entity_extraction(text, patterns)
        if entities_ner_regex:
            for patternName, value in entities_ner_regex.items():
                if value:
                    if 'demande_' not in patternName:
                        if patternName == "numeroCarte":
                            entities_dict[patternName] = int(value[0].replace(" ",""))
                        else:
                            entities_dict[patternName] = value[0]
                    else:
                        request_list.append(patternName)
            response_body = {"userId": userid, "entitiesDic": entities_dict}
            cartes = self.spring_api.post_data_check('cartes/searchEntitiesDict', response_body)
            return get_cartes_message(request_list, cartes)
        else:
            cartes = self.spring_api.get_data(f"cartes/user/{userid}")
            return get_cartes_message(request_list, cartes)

    def consultation_operations_action(self, data, patterns):
        text = data['text'].lower()
        userid = data['userId']
        entities_dict = {}
        entities_ner_bert, entities_ner_regex = entity_extraction(text, patterns)

        if entities_ner_regex is None:
            operations = self.spring_api.get_data(f"operations/user/{userid}")
        else:
            for patternName, value in entities_ner_regex.items():
                if value:
                    if patternName == "montant":
                        entities_dict = int(value[0])
                    else:
                        entities_dict[patternName] = value[0]
            response_body = {"userId": userid, "entitiesDic": entities_dict}
            print(response_body)
            operations = self.spring_api.post_data_check('operations/searchEntitiesDict', response_body)

        if operations:
            filtered_operations = filter_operations(operations, entities_ner_bert)
            return get_operations_message(operations, filtered_operations)
        else:
            return "Désolé, je n'ai trouvé aucune opération associée à votre compte."

    def action_process_beneficiaire(self, data, patterns, required_entities, action_type):
        text = data['text'].lower()
        userid = data['userId']
        entities_ner_bert, entities_ner_regex = entity_extraction(text, patterns)
        extracted_entities = {}
        missing_entities = []
        nom, prenom, rib, newRib, typeBeneficiaire = None, None, None, None, None
        if entities_ner_bert:
            prenom, nom = extract_names(entities_ner_bert)
        if entities_ner_regex:
            typeBeneficiaire, rib, newRib = self.intent_utils.extract_entities(entities_ner_regex, userid, action_type)
        for entity in required_entities:
            if locals().get(entity) is None:
                missing_entities.append(entity)
            else:
                extracted_entities[entity] = locals().get(entity)
        missing_entities = list(set(missing_entities))
        if missing_entities:
            return self.intent_utils.handle_missing_entity(missing_entities, MATCHING_PATTERNS, extracted_entities,
                                                           f"Gestion_Bénéficiare_{action_type.capitalize()}")
        else:
            if action_type == "ajout":
                message = f"Vous voulez ajouter un bénéficiaire avec le nom {nom}, le prénom {prenom} comme personne {typeBeneficiaire} ayant pour rib {rib}. Merci de confirmer cette demande!"
                return f"Request_Validation_Gestion_Bénéficiare_{action_type.capitalize()}", message, extracted_entities
            elif action_type == "suppression":
                if rib: extracted_entities['rib'] = rib
                beneficiaries = self.intent_utils.check_beneficiaires(userid, extracted_entities)
                if beneficiaries:
                    if len(beneficiaries) == 1:
                        message = f"Vous voulez supprimer le bénéficiaire {nom} {prenom}. Merci de confirmer cette demande!"
                        return f"Request_Validation_Gestion_Bénéficiare_{action_type.capitalize()}", message, extracted_entities
                    else:
                        extracted_entities['besoin_ribBeneficiaire'] = None
                        if rib:
                            message = f"Vous voulez supprimer le bénéficiaire ayant le {rib}. Merci de confirmer cette demande!"
                            return f"Request_Validation_Gestion_Bénéficiare_{action_type.capitalize()}", message, extracted_entities
                        else:
                            message = f"Vous avez plusieurs bénéficiaires avec le même nom, merci de préciser le RIB du bénéficiaire!"
                            return f"Entity_Missing_Gestion_Bénéficiare_{action_type.capitalize()}", message, extracted_entities
                else:
                    message = "Aucun bénéficiaire ne correspond à ces noms!"
                    extracted_entities.pop('nom')
                    extracted_entities.pop('prenom')
                    return f"Entity_Missing_Gestion_Bénéficiare_{action_type.capitalize()}", message, extracted_entities
            elif action_type == "modification":
                if rib: extracted_entities['rib'] = rib
                beneficiaries = self.intent_utils.check_beneficiaires(userid, extracted_entities)
                if beneficiaries:
                    if len(beneficiaries) == 1:
                        message = f"Vous voulez modifier le bénéficiaire {nom} {prenom} avec le nouveau RIB {newRib}. Merci de confirmer cette demande!"
                        return "Request_Validation_Gestion_Bénéficiare_Modification", message, extracted_entities
                    else:
                        extracted_entities['besoin_ribBeneficiaire'] = None
                        if rib:
                            message = f"Vous voulez modifier le bénéficiaire {nom} {prenom} avec le nouveau RIB {newRib}. Merci de confirmer cette demande!"
                            return "Request_Validation_Gestion_Bénéficiare_Modification", message, extracted_entities
                        else:
                            message = f"Vous avez plusieurs bénéficiaires avec le même nom, merci préciser le RIB du bénéficiaire!"
                            return "Entity_Missing_Gestion_Bénéficiare_Modification", message, extracted_entities
                else:
                    message = "Aucun bénéficiaire ne correspond à ces noms!"
                    extracted_entities.pop('nom')
                    extracted_entities.pop('prenom')
                    return "Entity_Missing_Gestion_Bénéficiare_Suppression", message, extracted_entities

    def action_ajout_beneficiaire(self, data, patterns):
        required_entities = ['nom', 'prenom', 'rib', 'typeBeneficiaire']
        return self.action_process_beneficiaire(data, patterns, required_entities, "ajout")

    def action_suppression_beneficiaire(self, data, patterns):
        required_entities = ['nom', 'prenom']
        return self.action_process_beneficiaire(data, patterns, required_entities, "suppression")

    def action_modification_beneficiaire(self, data, patterns):
        required_entities = ['nom', 'prenom', 'newRib']
        return self.action_process_beneficiaire(data, patterns, required_entities, "modification")

    def action_services_cartes(self, data, patterns, status):
        global message
        text = data['text'].lower()
        userid = data['userId']
        _, entities_ner_regex = entity_extraction(text, patterns)
        required_entities = ['typeCarte', 'services']
        extracted_entities = {}
        missing_entities = []
        typeCarte, services, numeroCarte, numero_carte_str = None, None, None, None

        if entities_ner_regex:
            if entities_ner_regex.get('typeCarte'):
                typeCarte = entities_ner_regex.get('typeCarte')[0]
            if entities_ner_regex.get('services'):
                services = entities_ner_regex.get('services')
            if entities_ner_regex.get('numeroCarte'):
                numero_carte_str = entities_ner_regex.get('numeroCarte')[0].replace(" ", "")
                numeroCarte = int(numero_carte_str)

        for entity in required_entities:
            if locals().get(entity) is None:
                missing_entities.append(entity)
            else:
                extracted_entities[entity] = locals().get(entity)
        missing_entities = list(set(missing_entities))
        if numeroCarte: extracted_entities['numeroCarte'] = numeroCarte
        if missing_entities:
            return self.intent_utils.handle_missing_entity(missing_entities, MATCHING_PATTERNS, extracted_entities,
                                                           f"Gestion_Cartes_{status}")
        else:
            cartes = self.intent_utils.check_cartes(userid, extracted_entities)
            if cartes:
                if len(cartes) == 1:
                    if status == "Activation":
                        message = (
                            f"Vous voulez ajouter les services {', '.join(services)} pour votre carte {typeCarte}. Merci de "
                            f"confirmer cette demande!")
                        return f"Request_Validation_Gestion_Cartes_{status}", message, extracted_entities
                    elif status == "Désactivation":
                        message = (
                            f"Vous voulez désactiver les services {', '.join(services)} pour votre carte {typeCarte}. Merci de "
                            f"confirmer cette demande!")
                    return f"Request_Validation_Gestion_Cartes_{status}", message, extracted_entities
                else:
                    extracted_entities['besoin_numeroCarte'] = None
                    if numeroCarte:
                        message = (
                            f"Vous voulez ajouter les services {', '.join(services)} pour votre carte numéro {numeroCarte}. Merci de "
                            f"confirmer cette demande!")
                        return f"Request_Validation_Gestion_Cartes_{status}", message, extracted_entities
                    else:
                        message = (
                            f"Vous avez plusieurs cartes du même type, merci de préciser le numéro de votre carte "
                            f"bancaire!")
                        return f"Entity_Missing_Gestion_Cartes_{status}", message, extracted_entities
            else:
                message = "Aucune carte ne correspond aux informations que vous venez de donner!"
                extracted_entities.pop('typeCarte')
                return f"Entity_Missing_Gestion_Cartes_{status}", message, extracted_entities

    def action_activation_carte(self, data, patterns):
        return self.action_services_cartes(data, patterns, "Activation")

    def action_desactivation_carte(self, data, patterns):
        return self.action_services_cartes(data, patterns, "Désactivation")

    def action_opposition_carte(self, data, patterns):
        text = data['text'].lower()
        userid = data['userId']
        _, entities_ner_regex = entity_extraction(text, patterns)
        required_entities = ['typeCarte', 'raisonsOpposition']
        numero_carte_str, numeroCarte, typeCarte, raisonsOpposition = None, None, None, None
        extracted_entities = {}
        missing_entities = []
        if entities_ner_regex:
            if entities_ner_regex.get('typeCarte'):
                typeCarte = entities_ner_regex.get('typeCarte')[0]
            if entities_ner_regex.get('raisonsOpposition'):
                raisonsOpposition = entities_ner_regex.get('raisonsOpposition')[0]
            if entities_ner_regex.get('numeroCarte'):
                numero_carte_str = entities_ner_regex.get('numeroCarte')[0].replace(" ", "")
                if numero_carte_str:
                    numeroCarte = int(numero_carte_str)
        # Check for each required entity
        for entity in required_entities:
            if locals()[entity] is None:
                missing_entities.append(entity)
            else:
                extracted_entities[entity] = locals()[entity]
        # Remove duplicate entries in missing_entities, if any
        missing_entities = list(set(missing_entities))
        if numeroCarte: extracted_entities['numeroCarte'] = numeroCarte
        if missing_entities:
            return self.intent_utils.handle_missing_entity(missing_entities, MATCHING_PATTERNS, extracted_entities,
                                                           "Gestion_Cartes_Opposition")
        else:
            cartes = self.intent_utils.check_cartes(userid, extracted_entities)
            if cartes:
                if len(cartes) == 1:
                    message = (
                        f"Vous voulez opposer votre carte {typeCarte} pour {raisonsOpposition}. Merci de "
                        f"confirmer cette demande!")
                    return f"Request_Validation_Gestion_Cartes_Opposition", message, extracted_entities
                else:
                    extracted_entities['besoin_numeroCarte'] = None
                    if numeroCarte:
                        message = (
                            f"Vous voulez opposer votre carte {typeCarte} numéro {numeroCarte} pour {raisonsOpposition}. Merci de "
                            f"confirmer cette demande!")
                        return f"Request_Validation_Gestion_Cartes_Opposition", message, extracted_entities
                    else:
                        message = (
                            f"Vous avez plusieurs cartes du même type, merci de préciser le numéro de votre carte "
                            f"bancaire!")
                        return f"Entity_Missing_Gestion_Cartes_Opposition", message, extracted_entities
            else:
                message = "Aucune carte ne correspond aux informations données!"
                extracted_entities.pop('typeCarte')
                return 'f"Entity_Missing_Gestion_Cartes_Opposition"', message, extracted_entities

    def action_plafond_carte(self, data, patterns, status):
        global message
        text = data['text'].lower()
        userid = data['userId']
        _, entities_ner_regex = entity_extraction(text, patterns)
        required_entities = ['typeCarte', 'plafond', 'typePlafond']
        extracted_entities = {}
        missing_entities = []
        typeCarte, typePlafond, plafond, numeroCarte, card_number_str = None, None, None, None, None
        if entities_ner_regex:
            if entities_ner_regex.get('typeCarte'):
                typeCarte = entities_ner_regex.get('typeCarte')[0]
            if entities_ner_regex.get('typePlafond'):
                typePlafond = entities_ner_regex.get('typePlafond')[0]
            if entities_ner_regex.get('montant'):
                montant_str = entities_ner_regex.get('montant')[0]
                plafond = int(montant_str)
            if entities_ner_regex.get('numeroCarte'):
                card_number_str = entities_ner_regex.get('numeroCarte')[0].replace(" ", "")
                numeroCarte = int(card_number_str)

        for entity in required_entities:
            if locals().get(entity) is None:
                missing_entities.append(entity)
            else:
                extracted_entities[entity] = locals().get(entity)
        missing_entities = list(set(missing_entities))
        if numeroCarte: extracted_entities['numeroCarte'] = numeroCarte
        if missing_entities:
            return self.intent_utils.handle_missing_entity(missing_entities, MATCHING_PATTERNS, extracted_entities,
                                                           f"Gestion_Cartes_{status}_Plafond")
        else:
            cartes = self.intent_utils.check_cartes(userid, extracted_entities)
            if cartes:
                if len(cartes) == 1:
                    if status == "Augmenter":
                        message = (
                            f"Vous voulez augmenter le plafond de {typePlafond} pour votre carte {typeCarte}. Merci de "
                            f"confirmer cette demande!")
                    elif status == "Diminuer":
                        message = (
                            f"Vous voulez diminuer le plafond de {typePlafond} pour votre carte {typeCarte}. Merci de "
                            f"confirmer cette demande!")
                    return f"Request_Validation_Gestion_Cartes_{status}_Plafond", message, extracted_entities
                else:
                    extracted_entities['besoin_numeroCarte'] = None
                    if numeroCarte:
                        if status == "Augmenter":
                            message = (
                                f"Vous voulez augmenter le plafond de {typePlafond} pour votre carte numéro {numeroCarte}. Merci de "
                                f"confirmer cette demande!")
                        elif status == "Diminuer":
                            message = (
                                f"Vous voulez diminuer le plafond de {typePlafond} pour votre carte numéro {numeroCarte}. Merci de "
                                f"confirmer cette demande!")
                        return f"Request_Validation_Gestion_Cartes_{status}_Plafond", message, extracted_entities
                    else:
                        message = (
                            f"Vous avez plusieurs cartes du même type, merci de préciser le numéro de votre carte "
                            f"bancaire!")
                        return f"Entity_Missing_Gestion_Cartes_{status}_Plafond", message, extracted_entities
            else:
                message = "Aucune carte ne correspond à ces données"
                extracted_entities.pop('typeCarte')
                return f"Entity_Missing_Gestion_Cartes_{status}_Plafond", message, extracted_entities

    def action_plafond_augmenter(self, data, patterns):
        return self.action_plafond_carte(data, patterns, "Augmenter")

    def action_plafond_diminuer(self, data, patterns):
        return self.action_plafond_carte(data, patterns, "Diminuer")

    def action_ajout_transaction_virement(self, data, patterns):
        text = data['text'].lower()
        userid = data['userId']
        entities_ner_bert, entities_ner_regex = entity_extraction(text, patterns)
        required_entities = ['typeCompte', 'nom', 'prenom', 'montant', 'typeOperation']
        optional_entities = ['rib', 'numeroCompte', 'motif']
        extracted_entities = {}
        missing_entities = []
        nom, prenom, typeCompte, numeroCompte, rib, typeOperation, montant, motif = None, None, None, None, None, None, None, None

        if entities_ner_regex:
            if entities_ner_regex.get('typeCompte'):
                typeCompte = entities_ner_regex.get('typeCompte')[0]
            if entities_ner_regex.get('rib'):
                rib = entities_ner_regex.get('rib')[0].replace(" ", "")
            if entities_ner_regex.get('numeroCompte'):
                numeroCompte = entities_ner_regex.get('numeroCompte')[0].replace(" ", "").upper()
            if entities_ner_regex.get('typeOperation'):
                typeOperation = entities_ner_regex.get('typeOperation')[0]
            if entities_ner_regex.get('montant'):
                montant = int(entities_ner_regex.get('montant')[0])
            if entities_ner_regex.get('motif'):
                motif = entities_ner_regex.get('motif')[0]

        if entities_ner_bert:
            prenom, nom = extract_names(entities_ner_bert)
        for entity in required_entities:
            if locals().get(entity):
                extracted_entities[entity] = locals().get(entity)
            else:
                missing_entities.append(entity)
        for entity in optional_entities:
            if locals().get(entity):
                extracted_entities[entity] = locals().get(entity)
        missing_entities = list(set(missing_entities))
        if missing_entities:
            return self.intent_utils.handle_missing_entity(missing_entities, MATCHING_PATTERNS, extracted_entities,
                                                           'Transaction_Virement')
        else:
            return self.intent_utils.valid_account_num_rib(userid, extracted_entities)

    def action_ajout_transaction_paiement(self, data, patterns):
        text = data['text'].lower()
        userid = data['userId']
        _, entities_ner_regex = entity_extraction(text, patterns)
        required_entities = ['typeCompte', 'numeroFacture']
        extracted_entities = {}
        missing_entities = []
        typeCompte, numeroCompte, numeroFacture = None, None, None
        if entities_ner_regex:
            if entities_ner_regex.get('typeCompte'):
                typeCompte = entities_ner_regex.get('typeCompte')[0]
            if entities_ner_regex.get('numeroFacture'):
                numeroFacture = entities_ner_regex.get('numeroFacture')[0].upper()
            if entities_ner_regex.get('numeroCompte'):
                numeroCompte = entities_ner_regex.get('numeroCompte')[0].replace(" ", "").upper()
        for entity in required_entities:
            if locals().get(entity) is None:
                missing_entities.append(entity)
            else:
                extracted_entities[entity] = locals().get(entity)
        missing_entities = list(set(missing_entities))
        if numeroCompte: extracted_entities['numeroCompte'] = numeroCompte
        if missing_entities:
            return self.intent_utils.handle_missing_entity(missing_entities, MATCHING_PATTERNS, extracted_entities,
                                                           "Transaction_PaiementFacture")
        else:
            return self.intent_utils.valid_account_num_invoice(userid, extracted_entities)
