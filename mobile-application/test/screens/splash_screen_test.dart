import 'package:audioplayers/audioplayers.dart';
import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:mocktail/mocktail.dart';
import 'package:test_rv/screens/biometrics_screen.dart';
import 'package:test_rv/screens/dialog_screen.dart';
import 'package:test_rv/screens/sign_in_screen.dart';
import 'package:test_rv/screens/splash_screen.dart';
import 'package:test_rv/size_config.dart';
import 'package:test_rv/utils/text_to_voice.dart';

class MockAudioPlayer extends Mock implements AudioPlayer {}

class MockTextToVoice extends Mock implements TextToVoice {
  @override
  Future<void> speak(String text) =>
      super.noSuchMethod(Invocation.method(#speak, [text]));
}
final Map<String, WidgetBuilder> mockRoutes = {
  DialogScreen.routeName: (context) => DialogScreen(),
  BiometricsScreen.routeName: (context) => BiometricsScreen(),
  SignInScreen.routeName: (context) => SignInScreen(),
};

void main() {
  late MockAudioPlayer mockAudioPlayer;
  late MockTextToVoice mockTextToVoice;

  setUp(() {
    mockAudioPlayer = MockAudioPlayer();
    mockTextToVoice = MockTextToVoice();
    registerFallbackValue(MyTypeFake());
  });

  testWidgets('SplashScreen displays logo and loading indicator',
          (WidgetTester tester) async {
        await tester.pumpWidget(
          MaterialApp(
            home: SplashScreen(),
            routes: mockRoutes,
          ),
        );
        await tester.pumpAndSettle(Duration(milliseconds: 13000));  // Verify that the logo image is displayed
        expect(find.byType(Image), findsOneWidget);

      });

  testWidgets('SplashScreen plays audio and speaks welcome message', (WidgetTester tester) async {
    when(() => mockAudioPlayer.setSource(any())).thenAnswer((_) async {});
    when(() => mockAudioPlayer.resume()).thenAnswer((_) async {});
    when(() => mockTextToVoice.speak(any())).thenAnswer((_) async {});

    await tester.pumpWidget(
      MaterialApp(
        home: Builder(
          builder: (context) {
            SizeConfig.init(context);
            return SplashScreen();
          },
        ),
        routes: mockRoutes,
      ),
    );

    // Wait for the audio and voice speaking tasks to complete
    await tester.pumpAndSettle(Duration(seconds: 2));

    expect(mockAudioPlayer.resume(), completes);

    // Ensure that the text-to-voice method is called with the correct message
    expect(mockTextToVoice.speak('Bienvenue sur Attijari Assistant!'), completes);
  });

  testWidgets('SplashScreen navigates to BiometricsScreen after delay',
          (WidgetTester tester) async {
        await tester.pumpWidget(
          MaterialApp(
            routes: {
              BiometricsScreen.routeName: (context) => const BiometricsScreen(),
            },
            home: Builder(
              builder: (context) {
                SizeConfig.init(context);
                return SplashScreen();
              },
            ),
          ),
        );

        // Wait for the navigation delay
        await tester.pump(const Duration(milliseconds: 13000));
        await tester.pumpAndSettle();

        // Verify that the navigation to BiometricsScreen occurred
        expect(find.byType(BiometricsScreen), findsOneWidget);
      });
}
class MyTypeFake extends Fake implements Source {}
