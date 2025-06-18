from chatbot_api.utils.util_functions import get_missing_entity_message


class ActionsUtils:
    def __init__(self, spring_api_service):
        self.spring_api = spring_api_service

    def handle_missing_entity(self, missing_entities, MATCHING_PATTERNS, extracted_entities, context):
        missing_entities_explication = [MATCHING_PATTERNS[key] for key in missing_entities if key in MATCHING_PATTERNS]
        message = get_missing_entity_message(missing_entities_explication)
        return f"Entity_Missing_{context}", message, extracted_entities

    def check_beneficiaires(self, userid, extracted_entities):
        nom = extracted_entities.get('nom')
        prenom = extracted_entities.get('prenom')

        response_body = {
            "userId": userid,
            "beneficiaire": {
                "nom": nom,
                "prenom": prenom
            }
        }
        beneficiaires = self.spring_api.post_data_check('beneficiaires/user/names', response_body)

        return beneficiaires

    def check_comptes(self, userid, extracted_entities):
        type_compte = extracted_entities.get('typeCompte')

        comptes = self.spring_api.get_data(f'comptes/userTypeCompte/{userid}/{type_compte}')

        return comptes

    def check_cartes(self, userid, extracted_entities):
        type_carte = extracted_entities.get('typeCarte')

        cartes = self.spring_api.get_data(f'cartes/userCardsByType/{userid}/{type_carte}')

        return cartes

    def which_rib(self, userid, ribs):
        rib = ribs[0].replace(" ","")
        response_body = {
            "userId": userid,
            "beneficiaire": rib
        }
        beneficiaire = self.spring_api.post_data_check('beneficiaires/user/rib', response_body)

        return beneficiaire

    def extract_entities(self, entities_ner_regex, userid, action_type):
        typeBeneficiaire, rib, newRib = self.extract_additional_info(entities_ner_regex, action_type, userid)
        return typeBeneficiaire, rib, newRib

    def extract_additional_info(self, entities, action_type, userid):
        typeBeneficiaire = None
        rib, newRib = None, None
        if entities.get('typeBeneficiaire'):
            typeBeneficiaire = entities.get('typeBeneficiaire')[0]
        if entities.get('rib'):
            ribs = [rib.replace(" ", "") for rib in entities.get('rib')]
            rib, newRib = self.determine_ribs(ribs, action_type, userid)
        return typeBeneficiaire, rib, newRib

    def determine_ribs(self, ribs, action_type, userid):
        if len(ribs) == 2:
            return (ribs[0], ribs[1]) if self.which_rib(userid, ribs) else (
                ribs[1], ribs[0])
        else:
            if action_type == "modification":
                return (ribs[0], None) if self.which_rib(userid, ribs) else (None, ribs[0])
            else:
                return ribs[0], None

    def determine_ribs_complete_action(self, ribs, action_type, userid, context, extracted_entities):
        if len(ribs) == 2:
            # Determine the primary RIB based on some user-specific condition
            return (ribs[0], ribs[1]) if self.which_rib(userid, ribs) else (
                ribs[1], ribs[0])
        else:
            if action_type == "modification" and context == "Missing_Entity":
                # Decide based on what's currently missing and what the user provided
                if 'besoin_ribBeneficiaire' in extracted_entities:
                    if 'rib' in extracted_entities:
                        return (extracted_entities['rib'], ribs[0])
                    else:
                        return (ribs[0], None) if self.which_rib(userid, ribs) else (None, ribs[0])
                else:
                    return (None, ribs[0])
            else:
                return (ribs[0], None)

    def extract_additional_info_complete(self, entities, action_type, userid, context, extracted_entities):
        typeBeneficiaire, rib, newRib = None, None, None
        if entities.get('typeBeneficiaire'):
            typeBeneficiaire = entities.get('typeBeneficiaire')[0]
        if entities.get('rib'):
            ribs = entities.get('rib')
            rib, newRib = self.determine_ribs_complete_action(ribs, action_type, userid, context, extracted_entities)
        return typeBeneficiaire, rib, newRib

    def valid_account_num_rib(self, userid, extracted_entities):
        comptes = self.check_comptes(userid, extracted_entities)
        beneficiaries = self.check_beneficiaires(userid, extracted_entities)
        if comptes and beneficiaries:
            if len(comptes) == 1:
                if len(beneficiaries) == 1:
                    message = (
                        f"Vous souhaitez passer un virement {extracted_entities.get('typeOperation')} pour {extracted_entities.get('nom')} {extracted_entities.get('prenom')} avec le montant {extracted_entities.get('montant')} dirhams avec votre compte {extracted_entities.get('typeCompte')}. Merci de "
                        f"confirmer cette demande!")
                    return f"Request_Validation_Transaction_Virement", message, extracted_entities
                else:
                    extracted_entities['besoin_ribBeneficiaire'] = None
                    if extracted_entities.get('rib'):
                        message = (
                            f"Vous souhaitez passer un virement {extracted_entities.get('typeOperation')} pour {extracted_entities.get('nom')} {extracted_entities.get('prenom')} avec le montant {extracted_entities.get('montant')} dirhams avec votre compte {extracted_entities.get('typeCompte')}. Merci de "
                            f"confirmer cette demande!")
                        return f"Request_Validation_Transaction_Virement", message, extracted_entities
                    else:
                        message = "Vous avez plusieurs bénéficiaires avec les mêmes noms, merci de préciser le numéro de RIB de votre bénéficiaire"
                        return f"Entity_Missing_Transaction_Virement", message, extracted_entities
            else:
                if len(beneficiaries) == 1:
                    extracted_entities['besoin_numeroCompte'] = None
                    if extracted_entities.get('numeroCompte'):
                        message = (
                            f"Vous souhaitez passer un virement {extracted_entities.get('typeOperation')} pour {extracted_entities.get('nom')} {extracted_entities.get('prenom')} avec le montant {extracted_entities.get('montant')} dirhams avec votre compte {extracted_entities.get('typeCompte')}. Merci de "
                            f"confirmer cette demande!")
                        return f"Request_Validation_Transaction_Virement", message, extracted_entities
                    else:
                        message = "Vous avez plusieurs comptes du même type, merci de préciser le numéro de compte avec lequel vous souhaiter passer le virement!"
                        return f"Entity_Missing_Transaction_Virement", message, extracted_entities
                else:
                    extracted_entities['besoin_ribBeneficiaire'] = None
                    extracted_entities['besoin_numeroCompte'] = None
                    if extracted_entities.get('numeroCompte') is not None and extracted_entities.get('rib') is not None:
                        message = (
                            f"Vous souhaitez passer un virement {extracted_entities.get('typeOperation')} pour {extracted_entities.get('nom')} {extracted_entities.get('prenom')} avec le montant {extracted_entities.get('montant')} dirhams avec votre compte {extracted_entities.get('typeCompte')}. Merci de "
                            f"confirmer cette demande!")
                        return f"Request_Validation_Transaction_Virement", message, extracted_entities
                    else:
                        message = "Vous avez plusieurs comptes du même type et plusieurs bénéficiaires avec les mêmes noms, merci de préciser le numéro de compte et le RIB du bénéficiare avec lesquels vous souhaiter passer le virement!"
                        return f"Entity_Missing_Transaction_Virement", message, extracted_entities
        else:
            message = "Le type de compte ou les noms du bénéficiaires sont incorrectes!"
            extracted_entities.pop('typeCompte')
            extracted_entities.pop('nom')
            extracted_entities.pop('prenom')
            return f"Entity_Missing_Transaction_Virement", message, extracted_entities

    def valid_account_num_invoice(self, userid, extracted_entities):
        comptes = self.check_comptes(userid, extracted_entities)
        if comptes:
            if len(comptes) == 1:
                message = (
                    f"Vous voulez payer la facture {extracted_entities.get('numeroFacture')} avec votre compte {extracted_entities.get('typeCompte')}. Merci de "
                    f"confirmer cette demande!")
                return "Request_Validation_Transaction_PaiementFacture", message, extracted_entities
            else:
                extracted_entities['besoin_numeroCompte'] = None
                if extracted_entities.get('numeroCompte'):
                    message = (
                        f"Vous voulez payer la facture {extracted_entities.get('numeroFacture')} avec votre compte {extracted_entities.get('typeCompte')} de numéro {extracted_entities.get('numeroCompte')}. Merci de "
                        f"confirmer cette demande!")
                    return "Request_Validation_Transaction_PaiementFacture", message, extracted_entities
                else:
                    message = (
                        f"Vous avez plusieurs comptes du même type, merci de préciser le numéro de votre compte "
                        f"bancaire!")
                    return "Entity_Missing_Transaction_PaiementFacture", message, extracted_entities
        else:
            message = "Aucun compte ne correspond au type que vous avez précisé!"
            extracted_entities.pop('typeCompte')
            return "Entity_Missing_Transaction_PaiementFacture", message, extracted_entities
