import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:mocktail/mocktail.dart';
import 'package:speech_to_text/speech_recognition_error.dart';
import 'package:speech_to_text/speech_recognition_result.dart';
import 'package:speech_to_text/speech_to_text.dart';
import 'package:test_rv/screens/dialog_screen.dart';
import 'package:test_rv/screens/sign_in_screen.dart';

// Mock SpeechToText class
class MockSpeechToText extends Mock implements SpeechToText {
  @override
  Future<bool> initialize({
    dynamic debugLogging,
    Duration? finalTimeout,
    void Function(SpeechRecognitionError)? onError,
    void Function(String)? onStatus,
    List<SpeechConfigOption>? options,
  }) async {
    return true; // Return a Future<bool> with a value of true
  }
}

void main() {
  late MockSpeechToText mockSpeechToText;

  setUpAll(() {
    mockSpeechToText = MockSpeechToText();
    when(() => mockSpeechToText.initialize()).thenAnswer((_) async => true);
    when(() => mockSpeechToText.listen(onResult: any(named: 'onResult'))).thenAnswer((_) async {});
  });

  setUp(() {
    reset(mockSpeechToText); // Reset the mock before each test
    when(() => mockSpeechToText.hasPermission).thenAnswer((_) async => true);
  });

  testWidgets('Sign-in screen test', (WidgetTester tester) async {
    await tester.pumpWidget(MaterialApp(
      home: SignInScreen(), // Pass mock instance to the screen
    ));

    // Verify that the initial message is spoken
    expect(await mockSpeechToText.initialize(), true);
    expect(await mockSpeechToText.hasPermission, true);
    await tester.pump(const Duration(seconds: 8));

    // Tap on the screen to start listening
    await tester.tap(find.byType(GestureDetector).first); // Locate the first GestureDetector
    await tester.pump();

    // Verify that speech is started
    expect(verify(() => mockSpeechToText.listen(onResult: any(named: 'onResult'))), true);

    // Simulate speech result
    await tester.pumpWidget(MaterialApp(
      home: SignInScreen(), // Pass mock instance to the screen
    ));
    await tester.tap(find.byType(GestureDetector).first); // Locate the first GestureDetector
    await tester.pump();
    final result = SpeechRecognitionResult(
      'username' as List<SpeechRecognitionWords>, // Simulate recognized username
      true,
    );
    final onResult = verify(() => mockSpeechToText.listen(onResult: captureAny(named: 'onResult'))).captured.last as void Function(SpeechRecognitionResult);
    onResult(result);

    // Simulate next question
    await tester.pumpWidget(MaterialApp(
      home: SignInScreen(), // Pass mock instance to the screen
    ));
    await tester.tap(find.byType(GestureDetector).first); // Locate the first GestureDetector
    await tester.pump();
    final result2 = SpeechRecognitionResult(
      'password' as List<SpeechRecognitionWords>, // Simulate recognized password
      true,
    );
    onResult(result2);

    // Simulate confirmation
    await tester.pumpWidget(MaterialApp(
      home: SignInScreen(), // Pass mock instance to the screen
    ));
    await tester.tap(find.byType(GestureDetector).first); // Locate the first GestureDetector
    await tester.pump();
    final result3 = SpeechRecognitionResult(
      'oui' as List<SpeechRecognitionWords>, // Simulate confirmation of saving credentials
      true,
    );
    onResult(result3);

    // Ensure proper navigation
    expect(find.byType(DialogScreen), findsOneWidget);
  });
}
