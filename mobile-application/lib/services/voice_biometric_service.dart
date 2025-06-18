import 'dart:convert';
import 'dart:io';
import 'package:http/http.dart' as http;

class VoiceBiometricService {
  static const String apiUrl = 'http://192.168.100.35:8080';
  static const String authenticateEndpoint = '/users/authenticate-voice-print';
  static const String loginEndpoint = '/login';
  static const String apiUser = "username";
  static const String apiUserPassword = "password";
  static const String apiUserRole = "ADMIN";

  Future<Map<String, dynamic>?> authenticateUserByVoicePrint(File audioFile) async {
    try {
      // Authenticate user to get token
      final loginResponse = await http.post(
        Uri.parse(apiUrl + loginEndpoint),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'username': apiUser,
          'password': apiUserPassword,
          'role': apiUserRole,
        }),
      );

      if (loginResponse.statusCode == 200) {
        final loginData = jsonDecode(loginResponse.body);
        final token = loginData['token'];
        print('Token: $token');

        // Prepare multipart request
        var request = http.MultipartRequest('POST', Uri.parse(apiUrl + authenticateEndpoint))
          ..headers.addAll({
            'Authorization': 'Bearer $token',
          })
          ..files.add(await http.MultipartFile.fromPath('audio', audioFile.path));

        // Send request
        var response = await request.send();

        // Check response
        if (response.statusCode == 200) {
          // Read response body
          var responseBody = await response.stream.bytesToString();
          print(jsonDecode(responseBody));
          return jsonDecode(responseBody);
        } else {
          print('Failed to authenticate user: ${response.reasonPhrase}');
          return null;
        }
      } else {
        print('Authentication failed: ${loginResponse.body}');
        return null;
      }
    } catch (e) {
      print('Error during authentication: $e');
      return null;
    }
  }
}
