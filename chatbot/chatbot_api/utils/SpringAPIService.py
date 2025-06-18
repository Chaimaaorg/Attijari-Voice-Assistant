import requests


class SpringAPIService:
    # Class Variable to store the token
    nlp_tokens = []
    def __init__(self, base_url, username, password, role):
        self.base_url = base_url
        self.username = username
        self.password = password
        self.role = role

    def _get_token(self):
        login_url = f"{self.base_url}/login"
        payload = {
            "username": self.username,
            "password": self.password,
            "role": self.role
        }
        response = requests.post(login_url, json=payload)
        if response.status_code == 200:
            token_data = response.json()
            token = token_data.get('token')
            SpringAPIService.nlp_tokens.append(token)
            return token
        else:
            print(f"Error logging in: {response.status_code} - {response.text}")
            return None

    def _get_valid_token(self):
        # Check if there are any valid tokens in the list
        for token in self.nlp_tokens:
            if self._token_is_valid(token):
                return token
        # If no valid token is found, generate a new one
        return self._get_token()

    def _token_is_valid(self, token):

        url = f"{self.base_url}/api/check-token-validity"
        headers = {
            "Authorization": f"Bearer {token}",
            "Content-Type": "application/json"
        }
        response = requests.post(url, headers=headers)
        if response.status_code == 200:
            return True
        else:
            return False

    def _make_request(self, method, endpoint, data=None, success_status=None, error_status=None):
        global response
        token = self._get_valid_token()
        if token is None:
            return None  # Return None if token retrieval fails

        if success_status is None:
            success_status = {200}  # Default success status
        if error_status is None:
            error_status = set()  # Default empty error status set

        url = f"{self.base_url}/{endpoint}"
        headers = {
            "Authorization": f"Bearer {token}",
            "Content-Type": "application/json"
        }
        if method == "post":
            response = requests.post(url, headers=headers, json=data)
        elif method == "delete":
            response = requests.delete(url, headers=headers, json=data)
        elif method == "put":
            response = requests.put(url, headers=headers, json=data)
        elif method == "get":
            response = requests.get(url, headers=headers)
        if response.status_code in success_status:
            try:
                return response.json()
            except ValueError:
                return response.text
        elif response.status_code in error_status:
            return response.text
        # else:
        #     print(f"Unexpected response {response.status_code}: {response.text}")
        #     response.raise_for_status()

    def get_data(self, endpoint):
        return self._make_request('get', endpoint)

    def post_data_check(self, endpoint, data):
        return self._make_request('post', endpoint, data)

    def post_data_text(self, endpoint, data):
        return self._make_request('post', endpoint, data, success_status={200, 201}, error_status={400, 404})

    def delete_data(self, endpoint, data):
        return self._make_request('delete', endpoint, data, success_status={200, 204}, error_status={404, 400})

    def put_data(self, endpoint, data):
        return self._make_request('put', endpoint, data, success_status={200, 201}, error_status={400, 404})
