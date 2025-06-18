class TransactionsManager:
    def __init__(self, spring_api):
        self.spring_api = spring_api

    def add_transfer_by_account_type_names(self, userid, extracted_entities):
        response_body = {
            "userId": userid,
            "compte": {
                "typeCompte": extracted_entities.get('typeCompte')
            },
            "beneficiaire": {
                "nom": extracted_entities.get('nom'),
                "prenom": extracted_entities.get('prenom')
            },
            "operation": {
                "motif": extracted_entities.get('motif') if extracted_entities.get('motif') else "",
                "montant": extracted_entities.get('montant'),
                "typeOperation": extracted_entities.get('typeOperation')
            }
        }
        return self.spring_api.post_data_text('operations/addByAccountTypeBeneficiaryNames', response_body)

    def add_transfer_by_account_type_rib(self, userid, extracted_entities):
        response_body = {
            "userId": userid,
            "compte": {
                'typeCompte': extracted_entities.get('typeCompte')
            },
            "beneficiaire": {
                "rib": extracted_entities.get('rib')
            },
            "operation": {
                "motif": extracted_entities.get('motif') if extracted_entities.get('motif') else "",
                "montant": extracted_entities.get('montant'),
                "typeOperation": extracted_entities.get('typeOperation')
            }
        }
        return self.spring_api.post_data_text('operations/addByAccountTypeAndRib', response_body)

    def add_transfer_by_account_num_rib(self, userid, extracted_entities):
        response_body = {
            "userId": userid,
            "compte": {
                "numeroCompte": extracted_entities.get('numeroCompte')
            },
            "beneficiaire": {
                "rib": extracted_entities.get('rib')
            },
            "operation": {
                "motif": extracted_entities.get('motif') if extracted_entities.get('motif') else "",
                "montant": extracted_entities.get('montant'),
                "typeOperation": extracted_entities.get('typeOperation')
            }
        }
        return self.spring_api.post_data_text('operations/addByAccountNumAndRib', response_body)

    def add_transfer_by_account_num_names(self, userid, extracted_entities):
        response_body = {
            "userId": userid,
            "compte": {
                'numeroCompte': extracted_entities.get('numeroCompte'),

            },
            "beneficiaire": {
                "nom": extracted_entities.get('nom'),
                "prenom": extracted_entities.get('prenom'),
            },
            "operation": {
                'montant': extracted_entities.get('montant'),
                'typeOperation': extracted_entities.get('typeOperation')
            }
        }
        return self.spring_api.post_data_text('operations/addByAccountNumBeneficiaryNames', response_body)

    def add_invoice_by_account_type(self, userid, extracted_entities):
        response_body = {
            "userId": userid,
            "numeroFacture": extracted_entities.get('numeroFacture'),
            "typeCompte": extracted_entities.get('typeCompte')
        }
        return self.spring_api.post_data_text('paiement-facture/addInvoiceByAccountType', response_body)

    def add_invoice_by_account_num(self, userid, extracted_entities):
        response_body = {
            "userId": userid,
            "numeroFacture": extracted_entities.get('numeroFacture'),
            "numeroCompte": extracted_entities.get('numeroCompte'),
            "typeCompte": extracted_entities.get('typeCompte')
        }
        return self.spring_api.post_data_text('paiement-facture/addInvoiceByAccountNum', response_body)
