import 'dart:convert';
import 'package:http/http.dart' as http;

class AuthService {
  static const String apiUrl = 'http://192.168.100.35:8080';
  static const String loginEndpoint = '/login';
  static const String userInfoEndpoint = '/users/authenticate';
  static const String apiUser = "username";
  static const String apiUserPassword = "password";
  static const String apiUserRole = "ADMIN";

  Future<Map<String, dynamic>?> getUserInfo(String username,
      String password) async {
    final queryParams = {
      'username': username,
      'password': password,
    };
    try {
      final response = await http.post(
        Uri.parse(apiUrl + loginEndpoint),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'username': apiUser,
          'password': apiUserPassword,
          'role': apiUserRole,
        }),
      );

      if (response.statusCode == 200) {
        var data = jsonDecode(response.body);
        String token = data['token'];
        print('Token: $token');
        try {
          final uri = Uri.parse(apiUrl + userInfoEndpoint).replace(
            queryParameters: queryParams,
          );

          final userInfoResponse = await http.get(
            uri,
            headers: {
              'Authorization': 'Bearer $token',
              'Content-Type': 'application/json',
            },
          );
          if (userInfoResponse.statusCode == 200) {
            return jsonDecode(userInfoResponse.body);
          } else {
            print('Failed to retrieve user info: ${userInfoResponse.body}');
            return null;
          }
        } catch (e) {
          print('Error retrieving user info: $e');
          return null;
        }
      } else {
        print('Authentication failed: ${response.body}');
        return null;
      }
    } catch (e) {
      print('Error during authentication: $e');
      return null;
    }
  }
}
