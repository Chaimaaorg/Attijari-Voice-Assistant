import 'package:flutter/cupertino.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:mocktail/mocktail.dart';
import 'package:test_rv/models/utilisateur.dart';
import 'package:test_rv/providers/auth_provider.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:test_rv/services/auth_service.dart';

class MockAuthService extends Mock implements AuthService {}

class MockSharedPreferences extends Mock implements SharedPreferences {}

void main() {
  group('AuthProvider', () {
    WidgetsFlutterBinding.ensureInitialized();
    late AuthProvider authProvider;
    late MockAuthService mockAuthService;
    late MockSharedPreferences mockSharedPreferences;

    setUp(() {
      mockAuthService = MockAuthService();
      mockSharedPreferences = MockSharedPreferences();
      authProvider = AuthProvider(mockAuthService, mockSharedPreferences);
    });

    test('login should return user when credentials are valid', () async {
      // Arrange
      final validUsername = 'validUsername';
      final validPassword = 'validPassword';
      final userInfo = {'idUser': 1, 'username': validUsername, 'password': 'validPassword'};
      when(() => mockAuthService.getUserInfo(validUsername, validPassword))
          .thenAnswer((_) async => userInfo);

      // Act
      final user = await authProvider.login(validUsername, validPassword);

      // Assert
      expect(user, isA<Utilisateur>());
      expect(user!.userId, userInfo['idUser']);
      expect(user.username, userInfo['username']);
    });

    test('login should return null when credentials are invalid', () async {
      // Arrange
      final invalidUsername = 'invalidUsername';
      final invalidPassword = 'invalidPassword';
      when(() => mockAuthService.getUserInfo(invalidUsername, invalidPassword))
          .thenAnswer((_) async => null);

      // Act
      final user = await authProvider.login(invalidUsername, invalidPassword);

      // Assert
      expect(user, isNull);
    });

    test('saveCredentials should store username, password, and userId', () async {
      // Arrange
      final username = 'test_user';
      final password = 'test_password';
      final userId = 123;
      when(() => mockSharedPreferences.setString('username', username)).thenAnswer((_) async => true);
      when(() => mockSharedPreferences.setInt('userId', userId)).thenAnswer((_) async => true);
      when(() => mockSharedPreferences.setString('password', password)).thenAnswer((_) async => true);

      // Act
      await authProvider.saveCredentials(username, password, userId);

      // Assert
      verify(() => mockSharedPreferences.setString('username', username)).called(1);
      verify(() => mockSharedPreferences.setInt('userId', userId)).called(1);
      verify(() => mockSharedPreferences.setString('password', password)).called(1);
      verifyNever(() => mockSharedPreferences.remove(any())); // Ensure clearCredentials is not called
    });
  });
}
