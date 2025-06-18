import random

from chatbot_api.actions.ActionsUtils import ActionsUtils
from chatbot_api.actions.managers.beneficiary_manager import BeneficiaryManager
from chatbot_api.actions.managers.card_manager import CardManager
from chatbot_api.actions.managers.transactions_manager import TransactionsManager
from chatbot_api.utils.contstants import CANCEL_ACTION, MATCHING_PATTERNS
from chatbot_api.utils.nlu import entity_extraction
from chatbot_api.utils.util_functions import get_missing_entity_message, merge_entities, extract_names


class CompleteIntentActions:
    def __init__(self, spring_api_service):
        self.spring_api = spring_api_service
        self.utils = ActionsUtils(spring_api_service)
        self.beneficiary_manager = BeneficiaryManager(spring_api_service)
        self.card_manager = CardManager(spring_api_service)
        self.transactions_manager = TransactionsManager(spring_api_service)

    def action_complete_process_beneficiaires(self, data, extracted_entities, patterns, context, required_entities,
                                              action_type):
        text = data['text'].lower()
        userid = data['userId']
        entities_ner_bert, entities_ner_regex = entity_extraction(text, patterns)
        new_extracted_entities = {}
        missing_entities = []
        nom, prenom, rib, typBeneficiaire, newRib = None, None, None, None, None

        if entities_ner_bert:
            prenom, nom = extract_names(entities_ner_bert)
        if entities_ner_regex:
            typeBeneficiaire, rib, newRib = self.utils.extract_additional_info_complete(entities_ner_regex, action_type,
                                                                                        userid, context,
                                                                                        extracted_entities)
        for entity in required_entities:
            if locals().get(entity) is not None:
                new_extracted_entities[entity] = locals().get(entity)
        if action_type == "ajout":
            merged_entities = merge_entities(extracted_entities, new_extracted_entities)
            if context == "Confirmation_Action":
                message = self.beneficiary_manager.add_beneficiary(userid, extracted_entities)
                return "user_request", message, {}
            elif context == "Annulation_action":
                message = random.choice(CANCEL_ACTION)
                return "user_request", message, {}
            elif context == "Missing_Entity":
                for entity in required_entities:
                    if entity not in merged_entities:
                        missing_entities.append(entity)
                missing_entities_explication = [MATCHING_PATTERNS[key] for key in missing_entities if
                                                key in MATCHING_PATTERNS]
                if missing_entities:
                    message = get_missing_entity_message(missing_entities_explication)
                    return f"Entity_Missing_Gestion_Bénéficiare_{action_type.capitalize()}", message, merged_entities
                else:
                    message = f"Vous voulez ajouter un bénéficiaire avec le nom {merged_entities.get('nom')}, le prénom {merged_entities.get('prenom')} comme {merged_entities.get('typeBeneficiaire')} ayant pour rib {merged_entities.get('rib')}. Merci de confirmer cette demande!"
                    return f"Request_Validation_Gestion_Bénéficiare_{action_type.capitalize()}", message, merged_entities
        elif action_type == "suppression":
            if rib: new_extracted_entities['rib'] = rib
            merged_entities = merge_entities(extracted_entities, new_extracted_entities)
            if context == "Confirmation_Action":
                if 'besoin_ribBeneficiaire' not in extracted_entities.keys():
                    message = self.beneficiary_manager.delete_beneficiary_by_name(userid, extracted_entities)
                    return "user_request", message, {}
                else:
                    message = self.beneficiary_manager.delete_beneficiary_by_rib(userid, extracted_entities)
                    return "user_request", message, {}
            elif context == "Annulation_Action":
                message = random.choice(CANCEL_ACTION)
                return "user_request", message, {}
            elif context == "Missing_Entity":
                if 'besoin_ribBeneficiaire' in extracted_entities.keys():
                    if 'rib' not in merged_entities.keys():
                        message = "Veuillez préciser le RIB du bénéficiaire!"
                        return f"Entity_Missing_Gestion_Bénéficiare_{action_type.capitalize()}", message, merged_entities
                    else:
                        if all(entity in merged_entities.keys() for entity in required_entities):
                            beneficiary = self.beneficiary_manager.get_beneficiaries(userid, merged_entities.get('rib'))
                            if beneficiary:
                                message = f"Souhaitez-vous supprimer le bénéficiaire avec le RIB {rib} ? Merci de confirmer cette demande!"
                                return f"Request_Validation_Gestion_Bénéficiare_{action_type.capitalize()}", message, merged_entities
                            else:
                                message = f"Le RIB spécifié ne correspond à aucun bénéficiaire."
                                merged_entities.pop('rib')
                                return f"Entity_Missing_Gestion_Bénéficiare_{action_type.capitalize()}", message, merged_entities
                        else:
                            for entity in required_entities:
                                if entity not in merged_entities:
                                    missing_entities.append(entity)
                            missing_entities_explication = [MATCHING_PATTERNS[key] for key in missing_entities if
                                                            key in MATCHING_PATTERNS]
                            if missing_entities:
                                message = get_missing_entity_message(missing_entities_explication)
                                return f"Entity_Missing_Gestion_Bénéficiare_{action_type.capitalize()}", message, merged_entities
                else:
                    for entity in required_entities:
                        if entity not in merged_entities:
                            missing_entities.append(entity)
                    missing_entities_explication = [MATCHING_PATTERNS[key] for key in missing_entities if
                                                    key in MATCHING_PATTERNS]
                    if missing_entities:
                        message = get_missing_entity_message(missing_entities_explication)
                        return f"Entity_Missing_Gestion_Bénéficiare__{action_type.capitalize()}", message, merged_entities
                    else:
                        message = f"Vous voulez supprimer le bénéficiaire {merged_entities.get('nom')} {merged_entities.get('prenom')}. Merci de confirmer cette demande!"
                        return f"Request_Validation_Gestion_Bénéficiare_{action_type.capitalize()}", message, merged_entities
        elif action_type == "modification":
            if context == "Confirmation_Action":
                if 'besoin_ribBeneficiaire' not in extracted_entities:
                    message = self.beneficiary_manager.update_beneficiary_by_name(userid, extracted_entities)
                    return "user_request", message, {}
                else:
                    message = self.beneficiary_manager.update_beneficiary_by_rib(userid, extracted_entities)
                    return "user_request", message, {}
            elif context == "Annulation_Action":
                message = random.choice(CANCEL_ACTION)
                return "user_request", message, {}
            elif context == "Missing_Entity":
                if rib: new_extracted_entities['rib'] = rib
                merged_entities = merge_entities(extracted_entities, new_extracted_entities)
                if 'besoin_ribBeneficiaire' in extracted_entities:
                    if 'rib' not in merged_entities.keys():
                        message = "Veuillez préciser le RIB du bénéficiaire à modifier"
                        return f"Entity_Missing_Gestion_Bénéficiare_{action_type.capitalize()}", message, merged_entities
                    else:
                        if all(entity in merged_entities.keys() for entity in required_entities):
                            beneficiary = self.beneficiary_manager.get_beneficiaries(userid, merged_entities.get('rib'))
                            if beneficiary:
                                message = f"Souhaitez-vous modifier le bénéficiaire avec le RIB {rib} ? Merci de confirmer cette demande!"
                                return f"Request_Validation_Gestion_Bénéficiare_{action_type.capitalize()}", message, merged_entities
                            else:
                                message = f"Le RIB spécifié ne correspond à aucun bénéficiaire."
                                merged_entities.pop('rib')
                                return f"Entity_Missing_Gestion_Bénéficiare_{action_type.capitalize()}", message, merged_entities
                        else:
                            for entity in required_entities:
                                if entity not in merged_entities:
                                    missing_entities.append(entity)
                            missing_entities_explication = [MATCHING_PATTERNS[key] for key in missing_entities if
                                                            key in MATCHING_PATTERNS]
                            if missing_entities:
                                message = get_missing_entity_message(missing_entities_explication)
                                return f"Entity_Missing_Gestion_Bénéficiare_{action_type.capitalize()}", message, merged_entities
                else:
                    for entity in required_entities:
                        if entity not in merged_entities:
                            missing_entities.append(entity)
                    missing_entities_explication = [MATCHING_PATTERNS[key] for key in missing_entities if
                                                    key in MATCHING_PATTERNS]
                    if missing_entities:
                        message = get_missing_entity_message(missing_entities_explication)
                        return f"Entity_Missing_Gestion_Bénéficiare_{action_type.capitalize()}", message, merged_entities
                    else:
                        message = f"Vous voulez modifier le bénéficiaire {merged_entities.get('nom')} {merged_entities.get('prenom')} avec le RIB {merged_entities.get('newRib')}. Merci de confirmer cette demande!"
                        return f"Request_Validation_Gestion_Bénéficiare_{action_type.capitalize()}", message, merged_entities

    def action_complete_ajout_beneficiaire(self, data, extracted_entities, patterns, context):
        required_entities = ['nom', 'prenom', 'rib', 'typeBeneficiaire']
        return self.action_complete_process_beneficiaires(data, extracted_entities, patterns, context,
                                                          required_entities,
                                                          "ajout")

    def action_complete_suppression_beneficiaire(self, data, extracted_entities, patterns, context):
        required_entities = ['nom', 'prenom']
        return self.action_complete_process_beneficiaires(data, extracted_entities, patterns, context,
                                                          required_entities, "suppression")

    def action_complete_modification_beneficiaire(self, data, extracted_entities, patterns, context):
        required_entities = ['nom', 'prenom', 'newRib']
        return self.action_complete_process_beneficiaires(data, extracted_entities, patterns, context,
                                                          required_entities, "modification")

    def action_complete_services_cartes(self, data, extracted_entities, patterns, context, status):
        text = data['text'].lower()
        userid = data['userId']
        required_entities = ['typeCarte', 'services']
        _, entities_ner_regex = entity_extraction(text, patterns)
        new_extracted_entities = {}
        missing_entities = []
        typeCarte, services, numeroCarte, card_number_str = None, None, None, None

        if entities_ner_regex:
            if entities_ner_regex.get('typeCarte'):
                typeCarte = entities_ner_regex.get('typeCarte')[0]
            if entities_ner_regex.get('services'):
                services = entities_ner_regex.get('services')
            if entities_ner_regex.get('numeroCarte'):
                card_number_str = entities_ner_regex.get('numeroCarte')[0].replace(" ", "")
                numeroCarte = int(card_number_str)

        for entity in required_entities:
            if locals().get(entity) is not None:
                new_extracted_entities[entity] = locals().get(entity)
        if numeroCarte: new_extracted_entities['numeroCarte'] = numeroCarte
        merged_entities = merge_entities(extracted_entities, new_extracted_entities)
        if context == "Confirmation_Action":
            if all(entity in extracted_entities.keys() for entity in required_entities):
                message = self.card_manager.update_card_services_by_type(userid, extracted_entities, status)
                return "user_request", message, {}
            elif all(entity in extracted_entities.keys() for entity in
                     required_entities) and 'besoin_numeroCarte' in extracted_entities:
                message = self.card_manager.update_card_services_by_number(userid, extracted_entities, status)
                return "user_request", message, {}
        elif context == "Annulation_Action":
            message = random.choice(CANCEL_ACTION)
            return "user_request", message, {}
        elif context == "Missing_Entity":
            if 'besoin_numeroCarte' in extracted_entities.keys():
                if 'numeroCarte' not in merged_entities.keys():
                    message = "Veuillez préciser votre numéro de carte!"
                    return f"Entity_Missing_Gestion_Cartes_{status}", message, merged_entities
                else:
                    if all(entity in merged_entities.keys() for entity in required_entities):
                        message = self.spring_api.get_data(f'/userCardsByNum/{numeroCarte}')
                        if message:
                            message = f"Souhaitez-vous modifier les services permis par votre carte {typeCarte} numéro {numeroCarte} ? Merci de confirmer cette demande!"
                            return f"Request_Validation_Gestion_Cartes_{status}", message, merged_entities
                        else:
                            message = f"Aucune carte ne correspond à ce numéro!"
                            merged_entities.pop('numeroCarte')
                            return f"Entity_Missing_Gestion_Cartes_{status}", message, merged_entities
                    else:
                        for entity in required_entities:
                            if entity not in merged_entities:
                                missing_entities.append(entity)
                        missing_entities_explication = [MATCHING_PATTERNS[key] for key in missing_entities if
                                                        key in MATCHING_PATTERNS]
                        if missing_entities:
                            message = get_missing_entity_message(missing_entities_explication)
                            return f"Entity_Missing_Gestion_Cartes_{status}", message, merged_entities
            else:
                for entity in required_entities:
                    if entity not in merged_entities:
                        missing_entities.append(entity)
                missing_entities = list(set(missing_entities))
                missing_entities_explication = [MATCHING_PATTERNS[key] for key in missing_entities if
                                                key in MATCHING_PATTERNS]
                if missing_entities:
                    message = get_missing_entity_message(missing_entities_explication)
                    return f"Entity_Missing_Gestion_Cartes_{status}", message, merged_entities
                else:
                    message = f"Souhaitez-vous modifier les services {merged_entities.get('services')} permis par votre carte {merged_entities.get('typeCarte')} ? Merci de confirmer cette demande!"
                    return f"Request_Validation_Gestion_Cartes_{status}", message, merged_entities

    def action_complete_activation_carte(self, data, extracted_entities, patterns, context):
        return self.action_complete_services_cartes(data, extracted_entities, patterns, context, "Activation")

    def action_complete_desactivation_carte(self, data, extracted_entities, patterns, context):
        return self.action_complete_services_cartes(data, extracted_entities, patterns, context, "Désactivation")

    def action_complete_opposition_carte(self, data, extracted_entities, patterns, context):
        text = data['text'].lower()
        userid = data['userId']
        required_entities = ['typeCarte', 'raisonsOpposition']
        _, entities_ner_regex = entity_extraction(text, patterns)
        new_extracted_entities = {}
        missing_entities = []
        typeCarte, raisonsOpposition, numeroCarte, card_number_str = None, None, None, None

        if entities_ner_regex:
            if entities_ner_regex.get('typeCarte'):
                typeCarte = entities_ner_regex.get('typeCarte')[0]
            elif entities_ner_regex.get('raisonsOpposition'):
                raisonsOpposition = entities_ner_regex.get('raisonsOpposition')[0]
            elif entities_ner_regex.get('numeroCarte'):
                card_number_str = entities_ner_regex.get('numeroCarte')[0].replace(" ", "")
                if card_number_str:
                    numeroCarte = int(card_number_str)
        for entity in required_entities:
            if locals().get(entity) is not None:
                new_extracted_entities[entity] = locals().get(entity)
        if numeroCarte: new_extracted_entities['numeroCarte'] = numeroCarte
        merged_entities = merge_entities(extracted_entities, new_extracted_entities)
        if context == "Confirmation_Action":
            if 'besoin_numeroCarte' not in extracted_entities.keys():
                message = self.card_manager.oppose_card_by_type(userid, extracted_entities)
                return "user_request", message, {}
            else:
                message = self.card_manager.oppose_card_by_num(userid, extracted_entities)
                return "user_request", message, {}
        elif context == "Annulation_Action":
            message = random.choice(CANCEL_ACTION)
            return "user_request", message, {}
        elif context == "Missing_Entity":
            if 'besoin_numeroCarte' in extracted_entities.keys():
                if 'numeroCarte' not in merged_entities.keys():
                    message = "Veuillez préciser votre numéro de carte!"
                    return f"Entity_Missing_Gestion_Cartes_Opposition", message, merged_entities
                else:
                    if all(entity in merged_entities.keys() for entity in required_entities):
                        message = self.spring_api.get_data(f'/userCardsByNum/{numeroCarte}')
                        if message:
                            message = f"Souhaitez-vous opposer votre carte {merged_entities.get('typeCarte')} numéro {merged_entities.get('numeroCarte')} pour {merged_entities.get('raisonsOpposition')}? Merci de confirmer cette demande!"
                            return f"Request_Validation_Gestion_Cartes_Opposition", message, merged_entities
                        else:
                            message = f"Aucune carte ne correspond à ce numéro!"
                            return f"Entity_Missing_Gestion_Cartes_Opposition", message, merged_entities.pop(
                                'numeroCarte')
                    else:
                        for entity in required_entities:
                            if entity not in merged_entities:
                                missing_entities.append(entity)
                        missing_entities = list(set(missing_entities))
                        missing_entities_explication = [MATCHING_PATTERNS[key] for key in missing_entities if
                                                        key in MATCHING_PATTERNS]
                        if missing_entities:
                            message = get_missing_entity_message(missing_entities_explication)
                            return f"Entity_Missing_Gestion_Cartes_Opposition", message, merged_entities
            else:
                for entity in required_entities:
                    if entity not in merged_entities:
                        missing_entities.append(entity)
                missing_entities = list(set(missing_entities))
                missing_entities_explication = [MATCHING_PATTERNS[key] for key in missing_entities if
                                                key in MATCHING_PATTERNS]
                if missing_entities:
                    message = get_missing_entity_message(missing_entities_explication)
                    return "Entity_Missing_Gestion_Cartes_Opposition", message, merged_entities
                else:
                    message = f"Souhaitez-vous opposer votre carte {merged_entities.get('typeCarte')} pour {merged_entities.get('raisonsOpposition')}? Merci de confirmer cette demande!"
                    return "Request_Validation_Gestion_Cartes_Opposition", message, merged_entities

    def action_complete_plafond(self, data, extracted_entities, patterns, context, status):
        text = data['text'].lower()
        userid = data['userId']
        required_entities = ['typeCarte', 'typePlafond', 'plafond']
        _, entities_ner_regex = entity_extraction(text, patterns)
        new_extracted_entities = {}
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
            if locals().get(entity) is not None:
                new_extracted_entities[entity] = locals().get(entity)
        if numeroCarte: new_extracted_entities['numeroCarte'] = numeroCarte
        merged_entities = merge_entities(extracted_entities, new_extracted_entities)
        if context == "Confirmation_Action":
            if 'besoin_numeroCarte' not in extracted_entities.keys():
                message = self.card_manager.update_card_limit_by_type(userid, extracted_entities, status)
                return "user_request", message, {}
            else:
                message = self.card_manager.update_card_limit_by_num(userid, extracted_entities, status)
                return "user_request", message, {}
        elif context == "Annulation_Action":
            message = random.choice(CANCEL_ACTION)
            return "user_request", message, {}
        elif context == "Missing_Entity":
            if 'besoin_numeroCarte' in extracted_entities.keys():
                if 'numeroCarte' not in merged_entities.keys():
                    message = "Veuillez préciser votre numéro de carte!"
                    return f"Entity_Missing_Gestion_Cartes_{status}_Plafond", message, merged_entities
                else:
                    if all(entity in merged_entities.keys() for entity in required_entities):
                        message = self.spring_api.get_data(f'cartes/userCardsByNum/{numeroCarte}')
                        if message:
                            message = f"Souhaitez-vous {status.lower()} le plafond {merged_entities.get('typePlafond')} permis par votre carte {merged_entities.get('typeCarte')} numéro {numeroCarte} ? Merci de confirmer cette demande!"
                            return f"Request_Validation_Gestion_Cartes_{status}_Plafond", message, merged_entities
                        else:
                            message = f"Aucune carte ne correspond à ce numéro!"
                            merged_entities.pop('numeroCarte')
                            return f"Entity_Missing_Gestion_Cartes_{status}_Plafond", message, merged_entities
                    else:
                        for entity in required_entities:
                            if entity not in merged_entities:
                                missing_entities.append(entity)
                        missing_entities = list(set(missing_entities))
                        missing_entities_explication = [MATCHING_PATTERNS[key] for key in missing_entities if
                                                        key in MATCHING_PATTERNS]
                        if missing_entities:
                            message = get_missing_entity_message(missing_entities_explication)
                            return f"Entity_Missing_Gestion_Cartes_{status}_Plafond", message, merged_entities
            else:
                for entity in required_entities:
                    if entity not in merged_entities:
                        missing_entities.append(entity)
                missing_entities = list(set(missing_entities))
                missing_entities_explication = [MATCHING_PATTERNS[key] for key in missing_entities if
                                                key in MATCHING_PATTERNS]
                if missing_entities:
                    message = get_missing_entity_message(missing_entities_explication)
                    return f"Entity_Missing_Gestion_Cartes_{status}_Plafond", message, merged_entities
                else:
                    message = f"Souhaitez-vous {status.lower()} le plafond {merged_entities.get('typePlafond')} permis par votre carte {merged_entities.get('typeCarte')} ? Merci de confirmer cette demande!"
                    return f"Request_Validation_Gestion_Cartes_{status}_Plafond", message, merged_entities

    def action_complete_plafond_augmenter(self, data, extracted_entities, patterns, context):
        return self.action_complete_plafond(data, extracted_entities, patterns, context, "Augmenter")

    def action_complete_plafond_diminuer(self, data, extracted_entities, patterns, context):
        return self.action_complete_plafond(data, extracted_entities, patterns, context, "Diminuer")

    def action_complete_ajout_transaction_virement(self, data, extracted_entities, patterns, context):
        text = data['text'].lower()
        userid = data['userId']
        entities_ner_bert, entities_ner_regex = entity_extraction(text, patterns)
        required_entities = ['typeCompte', 'nom', 'prenom', 'montant', 'typeOperation']
        optional_entities = ['rib', 'numeroCompte', 'motif']
        new_extracted_entities = {}
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
                new_extracted_entities[entity] = locals().get(entity)
        for entity in optional_entities:
            if locals().get(entity):
                new_extracted_entities[entity] = locals().get(entity)
        merged_entities = merge_entities(extracted_entities, new_extracted_entities)
        if context == "Confirmation_Action":
            required_entities_rib_account_type = ['typeCompte', 'rib', 'montant', 'typeOperation']
            required_entities_rib_account_num = ['numeroCompte', 'rib', 'montant', 'typeOperation']
            required_entities_names_account_num = ['numeroCompte', 'nom', 'prenom', 'montant', 'typeOperation']
            if all(entity in extracted_entities.keys() for entity in
                   required_entities) and 'besoin_numeroCompte' not in extracted_entities.keys() and 'besoin_ribBeneficiaire' not in extracted_entities.keys():

                message = self.transactions_manager.add_transfer_by_account_type_names(userid, extracted_entities)
                return "user_request", message, {}
            elif all(entity in extracted_entities.keys() for entity in required_entities_rib_account_type):
                message = self.transactions_manager.add_transfer_by_account_type_rib(userid, extracted_entities)
                return "user_request", message, {}
            elif all(entity in extracted_entities.keys() for entity in required_entities_rib_account_num):
                message = self.transactions_manager.add_transfer_by_account_num_rib(userid, extracted_entities)
                return "user_request", message, {}
            elif all(entity in extracted_entities.keys() for entity in required_entities_names_account_num):
                message = self.transactions_manager.add_transfer_by_account_num_names(userid, extracted_entities)
                return "user_request", message, {}
        elif context == "Annulation_Action":
            message = random.choice(CANCEL_ACTION)
            return "user_request", message, {}
        elif context == "Missing_Entity":
            message = ""
            if 'besoin_numeroCompte' in extracted_entities.keys():
                if merged_entities.get('numeroCompte') is None:
                    message = "Veuillez préciser votre numéro de compte!"
                    return "Entity_Missing_Transaction_Virement", message, merged_entities
                else:
                    if 'besoin_ribBeneficiaire' in extracted_entities.keys():
                        if merged_entities.get('rib') is None:
                            message += "Veuillez préciser le RIB du bénéficiaire!"
                            return "Entity_Missing_Transaction_Virement", message, merged_entities
                        else:
                            if all(entity in merged_entities.keys() for entity in required_entities):
                                message = f"Vous souhaitez passer un virement {merged_entities.get('typeOperation')} pour {merged_entities.get('nom')} {merged_entities.get('prenom')} avec le montant {merged_entities.get('montant')} dirhams avec votre compte {merged_entities.get('typeCompte')}. Merci de confirmer!"
                                return "Request_Validation_Transaction_Virement", message, merged_entities
                            else:
                                for entity in required_entities:
                                    if entity not in merged_entities:
                                        missing_entities.append(entity)
                                missing_entities = list(set(missing_entities))
                                missing_entities_explication = [MATCHING_PATTERNS[key] for key in missing_entities if
                                                                key in MATCHING_PATTERNS]
                                if missing_entities:
                                    message = get_missing_entity_message(missing_entities_explication)
                                    return "Entity_Missing_Transaction_Virement", message, merged_entities
                    else:
                        if all(entity in merged_entities.keys() for entity in required_entities):
                            message = f"Vous souhaitez passer un virement {merged_entities.get('typeOperation')} pour {merged_entities.get('nom')} {merged_entities.get('prenom')} avec le montant {merged_entities.get('montant')} dirhams avec votre compte {merged_entities.get('typeCompte')}. Merci de confirmer!"
                            return "Request_Validation_Transaction_Virement", message, merged_entities
                        else:
                            for entity in required_entities:
                                if entity not in merged_entities:
                                    missing_entities.append(entity)
                            missing_entities = list(set(missing_entities))
                            missing_entities_explication = [MATCHING_PATTERNS[key] for key in missing_entities if
                                                            key in MATCHING_PATTERNS]
                            if missing_entities:
                                message = get_missing_entity_message(missing_entities_explication)
                                return "Entity_Missing_Transaction_Virement", message, merged_entities
            elif 'besoin_ribBeneficiaire' in extracted_entities.keys():
                if merged_entities.get('rib') is None:
                    message += "Veuillez préciser le RIB du bénéficiaire!"
                    return "Entity_Missing_Transaction_Virement", message, merged_entities
                else:
                    if 'besoin_numeroCompte' in extracted_entities.keys():
                        if merged_entities.get('numeroCompte') is None:
                            message += "Veuillez préciser le numéro de votre compte!"
                            return "Entity_Missing_Transaction_Virement", message, merged_entities
                        else:
                            if all(entity in merged_entities.keys() for entity in required_entities):
                                message = f"Vous souhaitez passer un virement {merged_entities.get('typeOperation')} pour {merged_entities.get('nom')} {merged_entities.get('prenom')} avec le montant {merged_entities.get('montant')} dirhams avec votre compte {merged_entities.get('typeCompte')}. Merci de confirmer!"
                                return "Request_Validation_Transaction_Virement", message, merged_entities
                            else:
                                for entity in required_entities:
                                    if entity not in merged_entities:
                                        missing_entities.append(entity)
                                missing_entities = list(set(missing_entities))
                                missing_entities_explication = [MATCHING_PATTERNS[key] for key in missing_entities if
                                                                key in MATCHING_PATTERNS]
                                if missing_entities:
                                    message = get_missing_entity_message(missing_entities_explication)
                                    return "Entity_Missing_Transaction_Virement", message, merged_entities
                    else:
                        if all(entity in merged_entities.keys() for entity in required_entities):
                            message = f"Vous souhaitez passer un virement {merged_entities.get('typeOperation')} pour {merged_entities.get('nom')} {merged_entities.get('prenom')} avec le montant {merged_entities.get('montant')} dirhams avec votre compte {merged_entities.get('typeCompte')}. Merci de confirmer!"
                            return "Request_Validation_Transaction_Virement", message, merged_entities
                        else:
                            for entity in required_entities:
                                if entity not in merged_entities:
                                    missing_entities.append(entity)
                            missing_entities = list(set(missing_entities))
                            missing_entities_explication = [MATCHING_PATTERNS[key] for key in missing_entities if
                                                            key in MATCHING_PATTERNS]
                            if missing_entities:
                                message = get_missing_entity_message(missing_entities_explication)
                                return "Entity_Missing_Transaction_Virement", message, merged_entities
            else:
                for entity in required_entities:
                    if entity not in merged_entities:
                        missing_entities.append(entity)
                missing_entities = list(set(missing_entities))
                missing_entities_explication = [MATCHING_PATTERNS[key] for key in missing_entities if
                                                key in MATCHING_PATTERNS]
                if missing_entities:
                    message = get_missing_entity_message(missing_entities_explication)
                    return "Entity_Missing_Transaction_Virement", message, merged_entities
                else:
                    return self.utils.valid_account_num_rib(userid, merged_entities)

    def action_complete_ajout_transaction_paiement(self, data, extracted_entities, patterns, context):
        text = data['text'].lower()
        userid = data['userId']
        required_entities = ['typeCompte', 'numeroFacture']
        _, entities_ner_regex = entity_extraction(text, patterns)
        new_extracted_entities = {}
        missing_entities = []
        typeCompte, numeroFacture, numeroCompte = None, None, None

        if entities_ner_regex:
            if entities_ner_regex.get('typeCompte'):
                typeCompte = entities_ner_regex.get('typeCompte')[0]
            if entities_ner_regex.get('numeroFacture'):
                numeroFacture = entities_ner_regex.get('numeroFacture')[0].upper()
            if entities_ner_regex.get('numeroCompte'):
                numeroCompte = entities_ner_regex.get('numeroCompte')[0].replace(" ", "").upper()
        for entity in required_entities:
            if locals().get(entity) is not None:
                new_extracted_entities[entity] = locals().get(entity)
        if numeroCompte: new_extracted_entities['numeroCompte'] = numeroCompte
        merged_entities = merge_entities(extracted_entities, new_extracted_entities)
        if context == "Confirmation_Action":
            if 'besoin_numeroCompte' not in extracted_entities.keys():
                message = self.transactions_manager.add_invoice_by_account_type(userid, extracted_entities)
                return "user_request", message, {}
            else:
                message = self.transactions_manager.add_invoice_by_account_num(userid, extracted_entities)
                return "user_request", message, {}
        elif context == "Annulation_Action":
            message = random.choice(CANCEL_ACTION)
            return "user_request", message, {}
        elif context == "Missing_Entity":
            if 'besoin_numeroCompte' in extracted_entities.keys():
                if merged_entities.get('numeroCompte') is None:
                    message = "Veuillez préciser votre numéro de compte!"
                    return "Entity_Missing_Transaction_PaiementFacture", message, merged_entities
                else:
                    account = None
                    account = self.spring_api.get_data(
                        f'comptes/userNumCompte/{userid}/{merged_entities.get("numeroCompte")}')
                    if account:
                        if all(entity in merged_entities.keys() for entity in required_entities):
                            message = f"Souhaitez-vous payer la facture numéro {merged_entities.get('numeroFacture')} avec le compte bancaire numéro {merged_entities.get('numeroCompte')} ? Merci de confirmer cette demande!"
                            return "Request_Validation_Transaction_PaiementFacture", message, merged_entities
                        else:
                            for entity in required_entities:
                                if entity not in merged_entities:
                                    missing_entities.append(entity)
                            missing_entities = list(set(missing_entities))
                            missing_entities_explication = [MATCHING_PATTERNS[key] for key in missing_entities if
                                                            key in MATCHING_PATTERNS]
                            if missing_entities:
                                message = get_missing_entity_message(missing_entities_explication)
                                return "Entity_Missing_Transaction_PaiementFacture", message, merged_entities
                    else:
                        message = f"Aucun compte bancaire ne correspond à ce numéro."
                        return "Entity_Missing_Transaction_PaiementFacture", message, merged_entities
            else:
                for entity in required_entities:
                    if entity not in merged_entities:
                        missing_entities.append(entity)
                missing_entities = list(set(missing_entities))
                missing_entities_explication = [MATCHING_PATTERNS[key] for key in missing_entities if
                                                key in MATCHING_PATTERNS]
                if missing_entities:
                    message = get_missing_entity_message(missing_entities_explication)
                    return "Entity_Missing_Transaction_PaiementFacture", message, merged_entities
                else:
                    return self.utils.valid_account_num_invoice(userid, merged_entities)
