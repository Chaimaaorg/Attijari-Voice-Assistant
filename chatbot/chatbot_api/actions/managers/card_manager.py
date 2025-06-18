class CardManager:
    def __init__(self, spring_api):
        self.spring_api = spring_api

    def update_card_services_by_type(self,userid,extracted_entities,status):
        response_body = {
            "userId": userid,
            "carte": {
                'typeCarte': extracted_entities.get('typeCarte'),
            },
            "services": extracted_entities.get('services'),
            "status": "enable" if status == "Activation" else "disable"
        }
        return self.spring_api.put_data('cartes/updateByCardType', response_body)

    def update_card_services_by_number(self,userid,extracted_entities,status):
        response_body = {
            "userId": userid,
            "carte": {
                'typeCarte': extracted_entities.get('typeCarte'),
                'numeroCarte': extracted_entities.get('numeroCarte')
            },
            "services": extracted_entities.get('services'),
            "status": "enable" if status == "Activation" else "disable"
        }
        return self.spring_api.put_data('cartes/updateByCardNum', response_body)

    def oppose_card_by_type(self,userid,extracted_entities):
        response_body = {
            "userId": userid,
            "carte": {
                'typeCarte': extracted_entities.get('typeCarte'),
                "raisonsOpposition": extracted_entities.get('raisonsOpposition')
            }
        }
        return self.spring_api.put_data('cartes/opposeByCardType', response_body)

    def oppose_card_by_num(self,userid,extracted_entities):
        response_body = {
            "userId": userid,
            "carte": {
                'typeCarte': extracted_entities.get('typeCarte'),
                "raisonsOpposition": extracted_entities.get('raisonsOpposition'),
                'numeroCarte': extracted_entities.get('numeroCarte')
            }
        }
        return self.spring_api.put_data('cartes/opposeByCardNum', response_body)

    def update_card_limit_by_type(self,userid,extracted_entities,status):
        response_body = {
            "userId": userid,
            "carte": {
                "typeCarte": extracted_entities.get('typeCarte')
            },
            "plafond": {
                "typePlafond": extracted_entities.get('typePlafond'),
                "montantPlafond": extracted_entities.get('plafond'),
            },
            "statut": "add" if status == "Augmenter" else "disable",
            "duration": ""
        }
        return self.spring_api.put_data('plafond/updateByCardType', response_body)

    def update_card_limit_by_num(self,userid,extracted_entities,status):
        response_body = {
            "userId": userid,
            "carte": {
                "typeCarte": extracted_entities.get('typeCarte'),
                'numeroCarte': extracted_entities.get('numeroCarte'),

            },
            "plafond": {
                "typePlafond": extracted_entities.get('typePlafond'),
                "montantPlafond": extracted_entities.get('plafond'),
            },
            "statut": "add" if status == "Augmenter" else "disable",
            "duration": ""
        }
        message = self.spring_api.put_data('plafond/updateByCardNum', response_body)