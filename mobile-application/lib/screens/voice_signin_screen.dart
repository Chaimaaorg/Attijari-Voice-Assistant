import 'dart:io';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:test_rv/constants.dart';
import 'package:flutter_sound/flutter_sound.dart';
import 'package:path_provider/path_provider.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:test_rv/providers/voice_auth_provider.dart';
import '../utils/text_to_voice.dart';
import '../size_config.dart';
import 'dialog_screen.dart';

class VoiceSignInScreen extends StatefulWidget {
  const VoiceSignInScreen({Key? key}) : super(key: key);

  static String routeName = "/VoiceSignInScreen";

  @override
  State<VoiceSignInScreen> createState() => _VoiceSignInScreenState();
}

class _VoiceSignInScreenState extends State<VoiceSignInScreen> {
  final recorder = FlutterSoundRecorder();
  final player = FlutterSoundPlayer();
  bool isRecording = false;
  String? recordedFilePath;

  @override
  void initState() {
    super.initState();
    TextToVoice.speak("Pour vous authentifier, appuyer sur l'écran pour enregistrer un audio où vous répéter 'Voice Assistant'!");
    initRecorder();
    initPlayer();
  }

  @override
  void dispose() {
    recorder.closeRecorder();
    player.closePlayer();
    super.dispose();
  }

  Future<void> initRecorder() async {
    final status = await Permission.microphone.request();
    if (status != PermissionStatus.granted) {
      throw 'Microphone permission not granted';
    }
    await recorder.openRecorder();
  }

  Future<void> initPlayer() async {
    await player.openPlayer();
  }

  Future<void> startRecording() async {
    final directory = await getTemporaryDirectory();
    final filePath = '${directory.path}/recording_${DateTime.now().millisecondsSinceEpoch}.wav';
    recordedFilePath = filePath;
    await recorder.startRecorder(
      toFile: filePath,
      codec: Codec.pcm16WAV,
    );
    setState(() {
      isRecording = true;
    });
  }

  Future<void> stopRecording() async {
    await recorder.stopRecorder();
    setState(() {
      isRecording = false;
    });
  }

  Future<void> _authenticate() async {
    try {
      final audioFile = File(recordedFilePath!);
      final voiceAuthProvider = Provider.of<VoiceAuthProvider>(context, listen: false);
      final currentUser = await voiceAuthProvider.login(audioFile);
      if (currentUser != null) {
        print(currentUser.userId);
        // await voiceAuthProvider.saveCredentials(currentUser.userId);
        await TextToVoice.speak(
          "Authentification réussie. Redirection vers l'écran suivant",
        );
        await Future.delayed(Duration(seconds: 3));
        Navigator.pushNamed(context, DialogScreen.routeName);
      } else {
        await TextToVoice.speak(
          "Désolé, vous n'êtes pas autorisé à utiliser ce service!",
        );
      }
    } catch (e) {
      print('Error during authentication: $e');
    }
  }

  void _stopRecordingAndAuthenticate() async {
    try {
      await stopRecording();
      await _authenticate();
    } catch (e) {
      print('Error stopping recording: $e');
    }
  }

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: () {
        if (!isRecording) {
          startRecording();
        } else {
          _stopRecordingAndAuthenticate();
        }
      },
      child: Scaffold(
        body: DecoratedBox(
          decoration: const BoxDecoration(
            gradient: kPrimaryGradientColor,
          ),
          child: SafeArea(
            child: Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                crossAxisAlignment: CrossAxisAlignment.center,
                children: [
                  const Text(
                    'Se connecter',
                    style: TextStyle(
                      color: Colors.white,
                      fontSize: 38,
                      fontWeight: FontWeight.w800,
                    ),
                  ),
                  const SizedBox(
                    height: 28,
                  ),
                  const Text(
                    "Utilisez votre empreinte vocales pour vous connecter à l'application!",
                    style: TextStyle(color: Colors.white, fontSize: 18),
                    textAlign: TextAlign.center,
                  ),
                  const SizedBox(
                    height: 28,
                  ),
                  SizedBox(
                    width: MediaQuery.of(context).size.width,
                    height: getProportionateScreenHeight(100),
                    child: Image.asset(
                      "assets/images/waves_removed.png",
                      fit: BoxFit.cover,
                    ),
                  ),
                  const SizedBox(
                    height: 28,
                  ),
                  ElevatedButton.icon(
                    onPressed: () {
                      if (!isRecording) {
                        startRecording();
                      } else {
                        _stopRecordingAndAuthenticate();
                      }
                    },
                    icon: Icon(
                      isRecording ? Icons.stop : Icons.record_voice_over_sharp,
                      color: Colors.black,
                    ),
                    label: Text(
                      isRecording ? 'Stop Recording' : 'Start Authentication',
                      style: TextStyle(
                          color: Colors.black,
                          fontWeight: FontWeight.bold,
                          fontSize: 15),
                    ),
                  ),
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }
}