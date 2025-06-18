import 'dart:convert';

import 'package:flutter_test/flutter_test.dart';
import 'package:http/http.dart' as http;
import 'package:mocktail/mocktail.dart';
import 'package:test_rv/services/auth_service.dart';

class MockHttpClient extends Mock implements http.Client {}

class MockAuthService extends Mock implements AuthService {}

void main() {
  group('AuthService', () {
    late AuthService authService;
    late http.Client mockHttpClient;

    setUp(() {
      mockHttpClient = MockHttpClient();
      authService = MockAuthService();
      registerFallbackValue(Uri.parse('http://example.com'));
    });

    test('getUserInfo returns user info when authentication is successful',
        () async {
      // Arrange
      const username = 'testuser';
      const password = 'testpassword';
      const token = 'mock_token';
      final Map<String, Object> userInfo = {'idUser': 1, 'name': 'Test User'};

      when(() => mockHttpClient.post(any(),
              headers: any(named: 'headers'), body: any(named: 'body')))
          .thenAnswer(
              (_) async => http.Response(jsonEncode({'token': token}), 200));

      when(() => mockHttpClient.get(any(), headers: any(named: 'headers')))
          .thenAnswer((_) async => http.Response(jsonEncode(userInfo), 200));

      when(() => authService.getUserInfo(username, password))
          .thenAnswer((_) async => userInfo);

      // Act
      final result = await authService.getUserInfo(username, password);

      // Assert
      expect(result, userInfo);
    });

    test('getUserInfo returns null when authentication fails', () async {
      // Arrange
      const username = 'testuser';
      const password = 'testpassword';

      when(() => mockHttpClient.post(any(),
              headers: any(named: 'headers'), body: any(named: 'body')))
          .thenAnswer((_) async => http.Response('', 401)); // Unauthorized

      when(() => authService.getUserInfo(username, password))
          .thenAnswer((_) async => null);

      // Act
      final result = await authService.getUserInfo(username, password);

      // Assert
      expect(result, isNull);
    });

    test('getUserInfo returns null when network error occurs', () async {
      // Arrange
      const username = 'testuser';
      const password = 'testpassword';

      when(() => mockHttpClient.post(any(),
          headers: any(named: 'headers'),
          body: any(named: 'body'))).thenThrow(Exception('Network error'));

      when(() => authService.getUserInfo(username, password))
          .thenAnswer((_) async => null);

      // Act
      final result = await authService.getUserInfo(username, password);

      // Assert
      expect(result, isNull);
    });

    test('getUserInfo returns null when server response is not successful',
        () async {
      // Arrange
      const username = 'testuser';
      const password = 'testpassword';

      when(() => mockHttpClient.post(any(),
              headers: any(named: 'headers'), body: any(named: 'body')))
          .thenAnswer((_) async => http.Response('', 500)); // Server error

      when(() => authService.getUserInfo(username, password))
          .thenAnswer((_) async => null);

      // Act
      final result = await authService.getUserInfo(username, password);

      // Assert
      expect(result, isNull);
    });
  });
}
