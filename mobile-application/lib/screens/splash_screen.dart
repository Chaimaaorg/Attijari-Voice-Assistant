import 'dart:async';
import 'package:flutter/material.dart';
import 'package:audioplayers/audioplayers.dart';
import 'package:test_rv/screens/voice_signin_screen.dart';
import '../size_config.dart';
import '../utils/text_to_voice.dart';
import 'biometrics_screen.dart';
import 'google_maps_screen.dart';

class SplashScreen extends StatefulWidget {
  static String routeName = "/splash";

  const SplashScreen({Key? key}) : super(key: key);

  @override
  _SplashScreenState createState() => _SplashScreenState();
}

class _SplashScreenState extends State<SplashScreen> {
  late AudioPlayer player;

  @override
  void initState() {
    super.initState();    // Create the audio player if it hasn't been initialized yet.
    player = AudioPlayer();

    // Set the release mode to keep the source after playback has completed.
    player.setReleaseMode(ReleaseMode.stop);

    // Start the player as soon as the app is displayed.
    WidgetsBinding.instance!.addPostFrameCallback((_) async {
      await player.setSource(AssetSource('audios/splash_audio.mp3'));
      await player.resume();
    });
    TextToVoice.speak("Bienvenue sur Attijari Assistant!");

    // Navigate to the next screen after a delay.
    navigateToNextScreen();
  }

  void navigateToNextScreen() async {
    Timer(const Duration(milliseconds: 13000), () {
      Navigator.pushReplacementNamed(context, VoiceSignInScreen.routeName);
    });
  }

  @override
  void dispose() {
    player.dispose(); // Dispose of the player when the widget is disposed.
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    SizeConfig.init(context);
    return Scaffold(
      backgroundColor: Colors.white,
      body: Padding(
        padding: const EdgeInsets.only(top: 50.0),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            Container(
              margin: const EdgeInsets.only(top: 5),
              child: Image.asset(
                'assets/images/logoSplash.png',
                height: 300.0,
                width: 300.0,
              ),
            ),
            const SizedBox(height: 20.0),
            const Center(
              child:ExcludeSemantics(
                excluding: true, // Set to false to include the button in the semantics tree
                child: Text(
                  'Croire en vous!',
                  textAlign: TextAlign.center,
                  style: TextStyle(
                    color: Colors.black,
                    fontSize: 20.0,
                    fontWeight: FontWeight.bold,
                  ),
                ),
                ),
              ),
            const Expanded(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  CircularProgressIndicator(
                    valueColor:
                    AlwaysStoppedAnimation<Color>(Color(0xFFDD072A)),
                  ),
                  SizedBox(height: 20.0),
                  Text('Chargement'),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}
