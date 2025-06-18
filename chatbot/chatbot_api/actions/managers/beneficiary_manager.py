class BeneficiaryManager:
    def __init__(self, api_service):
        self.api_service = api_service

    def add_beneficiary(self, user_id, data):
        response_body = {
            "userId": user_id,
            "beneficiaire": {
                "nom": data.get('nom'),
                "prenom": data.get("prenom"),
                "rib": data.get("rib"),
                "typeBeneficiaire": data.get('typeBeneficiaire')
            }
        }
        return self.api_service.post_data_text('beneficiaires/add', response_body)

    def delete_beneficiary_by_name(self, user_id, data):
        response_body = {
            "userId": user_id,
            "beneficiaire": {
                'nom': data.get('nom'),
                "prenom": data.get("prenom"),
            }
        }
        return self.api_service.delete_data('beneficiaires/delete/names', response_body)

    def delete_beneficiary_by_rib(self, user_id, data):
        response_body = {
            "userId": user_id,
            "beneficiaire": {
                'rib': data.get('rib')
            }
        }
        return self.api_service.delete_data('beneficiaires/delete/rib', response_body)

    def get_beneficiaries(self, user_id, rib):
        response_body = {
            "userId": user_id,
            "beneficiaire": {
                'rib': rib
            }
        }
        beneficiary = self.api_service.post_data_check('beneficiaires/user/rib', response_body)
        return beneficiary

    def update_beneficiary_by_name(self, userid, extracted_entities):
        response_body = {
            "userId": userid,
            "beneficiaire": {
                'nom': extracted_entities.get('nom'),
                "prenom": extracted_entities.get("prenom"),
            },
            "newRib": extracted_entities.get('newRib')
        }
        return self.api_service.put_data('beneficiaires/update/names', response_body)

    def update_beneficiary_by_rib(self, userid, extracted_entities):
        response_body = {
            "userId": userid,
            "oldRib": extracted_entities.get('rib'),
            "newRib": extracted_entities.get('newRib')
        }
        return self.api_service.put_data('beneficiaires/update/rib', response_body)
