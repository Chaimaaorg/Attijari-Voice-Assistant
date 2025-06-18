import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_tts/flutter_tts.dart';
import 'package:mocktail/mocktail.dart';
import 'package:provider/provider.dart';
import 'package:speech_to_text/speech_recognition_result.dart';
import 'package:speech_to_text/speech_to_text.dart';
import 'package:test_rv/components/description.dart' as my_description;
import 'package:test_rv/components/logo_and_user_prompt.dart';
import 'package:test_rv/components/mic_button.dart';
import 'package:test_rv/models/utilisateur.dart';
import 'package:test_rv/providers/auth_provider.dart';
import 'package:test_rv/providers/dialog_provider.dart';
import 'package:test_rv/screens/biometrics_screen.dart';
import 'package:test_rv/screens/dialog_screen.dart';
import 'package:test_rv/screens/sign_in_screen.dart';

class MockSpeechToText extends Mock implements SpeechToText {}

class MockFlutterTts extends Mock implements FlutterTts {}

class MockDialogProvider extends Mock implements DialogProvider {}

class MockAuthProvider extends Mock implements AuthProvider {}

// Define mock routes
final Map<String, WidgetBuilder> mockRoutes = {
  DialogScreen.routeName: (_) => DialogScreen(),
  BiometricsScreen.routeName: (_) => BiometricsScreen(),
  SignInScreen.routeName: (_) => SignInScreen(),
  // Define other routes if needed
};

void main() {
  late MockSpeechToText mockSpeechToText;
  late MockFlutterTts mockFlutterTts;
  late MockDialogProvider mockDialogProvider;
  late MockAuthProvider mockAuthProvider;

  setUp(() {
    mockSpeechToText = MockSpeechToText();
    mockFlutterTts = MockFlutterTts();
    mockDialogProvider = MockDialogProvider();
    mockAuthProvider = MockAuthProvider();
  });

  testWidgets('DialogScreen renders correctly', (WidgetTester tester) async {
    when(() => mockSpeechToText.isListening).thenReturn(false);
    when(() => mockSpeechToText.isAvailable).thenReturn(true);
    when(() => mockAuthProvider.currentUser)
        .thenReturn(Utilisateur(userId: 123, username: '', password: ''));

    await tester.pumpWidget(
      MultiProvider(
        providers: [
          ChangeNotifierProvider<AuthProvider>(create: (_) => mockAuthProvider),
          ChangeNotifierProvider<DialogProvider>(
              create: (_) => mockDialogProvider),
        ],
        child: const MaterialApp(
          home: DialogScreen(),
        ),
      ),
    );

    expect(find.byType(my_description.Description), findsOneWidget);
    expect(find.byType(LogoAndUserPrompt), findsOneWidget);
    expect(find.byType(MicButton), findsOneWidget);
  });

  testWidgets('DialogScreen handles speech recognition result', (WidgetTester tester) async {
    final speechResult = SpeechRecognitionResult(
      [SpeechRecognitionWords('Hello', 1)], // Mimicking the result
      true, // Success
    );

    await tester.pumpWidget(
      MaterialApp(
        home: DialogScreen(),
      ),
    );

    final dialogScreen = find.byType(DialogScreen);
    DialogScreen dialogScreenWidget = tester.widget(dialogScreen) as DialogScreen;

    // Call the handleSpeechRecognitionResult method with the mocked result and context
    await tester.pump(); // Rebuild the widget after calling the method

    // Now you can check if the UI has updated accordingly
    expect(find.text('Hello'), findsOneWidget);
  });
}