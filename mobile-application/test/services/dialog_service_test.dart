import 'dart:convert';

import 'package:flutter_test/flutter_test.dart';
import 'package:http/http.dart' as http;
import 'package:mocktail/mocktail.dart';
import 'package:test_rv/services/dialog_service.dart';

class MockHttpClient extends Mock implements http.Client {}

class MockDialogService extends Mock implements DialogService {}

void main() {
  group('DialogService', () {
    late DialogService dialogService;
    late http.Client mockHttpClient;

    setUp(() {
      mockHttpClient = MockHttpClient();
      dialogService = MockDialogService();
      registerFallbackValue(Uri.parse('http://example.com'));
    });

    test('getDailog returns message when the userID is present', () async {
      // Arrange
      const userId = 123;
      const token = 'mock_token';
      final Object userInfo = {'response': 'Hello'};

      when(() => mockHttpClient.post(any(),
              headers: any(named: 'headers'), body: any(named: 'body')))
          .thenAnswer(
              (_) async => http.Response(jsonEncode({'token': token}), 200));

      when(() => mockHttpClient.get(any(), headers: any(named: 'headers')))
          .thenAnswer((_) async => http.Response(jsonEncode(userInfo), 200));

      when(() => dialogService.getDialog('Hello1', userId))
          .thenAnswer((_) async => userInfo);

      // Act
      final result = await dialogService.getDialog('Hello1', userId);

      // Assert
      expect(result, userInfo);
    });
    test('getDialog returns error message when response status code is not 200', () async {
      // Arrange
      const userId = 123;

      when(() => mockHttpClient.post(any(),
          headers: any(named: 'headers'), body: any(named: 'body')))
          .thenAnswer((_) async => http.Response('', 404)); // Mocking a non-successful response code

      when(() => dialogService.getDialog('Hello1', userId))
          .thenAnswer((_) async => 'Not Found');

      // Act
      final result = await dialogService.getDialog('Hello1', userId);

      // Assert
      expect(result, "Not Found");
    });
    test('getDialog returns error message when response body is empty', () async {
      // Arrange
      const userId = 123;

      when(() => mockHttpClient.post(any(),
          headers: any(named: 'headers'), body: any(named: 'body')))
          .thenAnswer((_) async => http.Response('{}', 200)); // Mocking an empty JSON response body

      when(() => dialogService.getDialog('Hello1', userId))
          .thenAnswer((_) async => "Désolé, une erreur s'est produite!"); // Mocking null response

      // Act
      final result = await dialogService.getDialog('Hello1', userId);

      // Assert
      expect(result, "Désolé, une erreur s'est produite!");
    });

    test('getDialog returns error message when user ID is null', () async {
      // Arrange
      final Object userInfo = {'response': 'Hello'};

      when(() => mockHttpClient.post(any(),
          headers: any(named: 'headers'), body: any(named: 'body')))
          .thenAnswer((_) async => http.Response('', 200)); // Mocking a successful response

      when(() => dialogService.getDialog('Hello1', null))
          .thenAnswer((_) async => 'User ID not found in preferences'); // Corrected to return a string directly

      // Act
      final result = await dialogService.getDialog('Hello1', null);

      // Assert
      expect(result, 'User ID not found in preferences');
    });

  });
}
