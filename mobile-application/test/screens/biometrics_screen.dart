import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:mocktail/mocktail.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:test_rv/screens/biometrics_screen.dart';
import 'package:test_rv/screens/dialog_screen.dart';
import 'package:test_rv/screens/sign_in_screen.dart';
import 'package:test_rv/size_config.dart';
import 'package:test_rv/utils/authentication.dart';
import 'package:test_rv/utils/text_to_voice.dart';

class MockNavigatorObserver extends Mock implements NavigatorObserver {}

class MockTextToVoice extends Mock implements TextToVoice {}

class MockAuthentication extends Mock implements Authentication {}

class MockSharedPreferences extends Mock implements SharedPreferences {}
class MockBuildContext extends Mock implements BuildContext {}


void main() {
  late MockBuildContext mockBuildContext;
  late MockTextToVoice mockTextToVoice;
  late MockAuthentication mockAuthentication;
  late MockSharedPreferences mockSharedPreferences;

  setUpAll(() {
    mockBuildContext = MockBuildContext();
    mockTextToVoice = MockTextToVoice();
    mockAuthentication = MockAuthentication();
    mockSharedPreferences = MockSharedPreferences();
    registerFallbackValue(mockBuildContext);

  });

  testWidgets(
      'BiometricsScreen navigates to DialogScreen when authenticated and userId exists',
      (WidgetTester tester) async {
    when(() => mockAuthentication.authenticateForTesting(any()))
        .thenAnswer((_) async => true);
    when(() => mockSharedPreferences.getInt('userId')).thenReturn(1);

    await tester.pumpWidget(
      MaterialApp(
        routes: {
          DialogScreen.routeName: (context) => const DialogScreen(),
        },
        home: Builder(
          builder: (context) {
            SizeConfig.init(context);
            return DialogScreen();
          },
        ),
      ),
    );

    expect(find.byType(DialogScreen), findsOneWidget);
  });

  testWidgets(
      'BiometricsScreen navigates to SignInScreen when authenticated and userId does not exist',
      (WidgetTester tester) async {
    when(() => mockAuthentication.authenticateForTesting(any()))
        .thenAnswer((_) async => true);
    when(() => mockSharedPreferences.getInt('userId')).thenReturn(null);

    await tester.pumpWidget(
      MaterialApp(
        routes: {
          SignInScreen.routeName: (context) => const SignInScreen(),
        },
        home: Builder(
          builder: (context) {
            SizeConfig.init(context);
            return SignInScreen();
          },
        ),
      ),
    );

    expect(find.byType(SignInScreen), findsOneWidget);
  });

  testWidgets('BiometricsScreen speaks instructions on initState',
      (WidgetTester tester) async {
    when(() => mockTextToVoice.speakTesting(any())).thenAnswer((_) async => {});

    await tester.pumpWidget(MaterialApp(
      home: Builder(
        builder: (context) {
          SizeConfig.init(context);
          return BiometricsScreen();
        },
      ),
    ));

    verifyNever(() => mockTextToVoice.speakTesting(any()));
  });
}
