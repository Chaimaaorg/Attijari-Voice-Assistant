import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:mocktail/mocktail.dart';
import 'package:provider/provider.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:test_rv/providers/auth_provider.dart';
import 'package:test_rv/providers/dialog_provider.dart';
import 'package:test_rv/services/auth_service.dart';
import 'package:test_rv/services/dialog_service.dart';

class MockSharedPreferences extends Mock implements SharedPreferences {}
class MockAuthService extends Mock implements AuthService {}
class MockDialogService extends Mock implements DialogService {}

void main() {
  group('MyApp', () {
    late SharedPreferences mockSharedPreferences;
    late AuthService mockAuthService;
    late DialogService mockDialogService;

    setUp(() {
      mockSharedPreferences = MockSharedPreferences();
      mockAuthService = MockAuthService();
      registerFallbackValue(mockSharedPreferences);
      mockDialogService = MockDialogService();
    });

    testWidgets('should build without exploding', (WidgetTester tester) async {
      await tester.pumpWidget(
        MultiProvider(
          providers: [
            ChangeNotifierProvider<AuthProvider>(
              create: (_) => AuthProvider(mockAuthService, mockSharedPreferences),
            ),
            ChangeNotifierProvider<DialogProvider>(
              create: (_) => DialogProvider(mockDialogService),
            ),
          ],
          child: const MaterialApp(),
        ),
      );

      expect(find.byType(MaterialApp), findsOneWidget);
    });
  });
}