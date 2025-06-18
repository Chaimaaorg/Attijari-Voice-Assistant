class SecurityValidator:
    def __init__(self, spring_api_service, userid):
        self.spring_api = spring_api_service
        self.userId = userid

    def provide_date_of_birth(self):
        user_info = self.spring_api.get_data(f"users/{self.userId}")
        if user_info:
            return user_info.get('client').get('dateNaissance')

    def provide_user(self):
        user_info = self.spring_api.get_data(f"users/{self.userId}")
        if user_info:
            return user_info

    def provide_email_address(self):
        user_info = self.spring_api.get_data(f"users/{self.userId}")
        if user_info:
            return user_info.get('email')

    def provide_postal_address(self):
        user_info = self.spring_api.get_data(f"users/{self.userId}")
        if user_info:
            return user_info.get('client').get('adresse')

    def provide_phone_number(self):
        user_info = self.spring_api.get_data(f"users/{self.userId}")
        if user_info:
            return user_info.get('telephone')

    def provide_last_transaction_amount(self):
        transactions = self.spring_api.get_data(f"operations/user/{self.userId}")
        if transactions:
            # Filter transactions if not None
            valid_transactions = [transaction for transaction in transactions if transaction is not None]
            if valid_transactions:
                # Sort valid transactions
                sorted_transactions = sorted(valid_transactions, key=lambda x: x.get('dateOperation'), reverse=True)
                return sorted_transactions[0].get('montant') if sorted_transactions else None
        return None

    def provide_agency(self):
        agencies = self.spring_api.get_data(f"agences/user/{self.userId}")
        if agencies:
            return agencies[0].get('nomAgence')
