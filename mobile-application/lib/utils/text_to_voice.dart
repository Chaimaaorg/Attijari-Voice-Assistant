import 'package:flutter_tts/flutter_tts.dart';

class TextToVoice {
  static final FlutterTts flutterTts = FlutterTts();

  static Future<void> speak(String message, {Function? onDone}) async {
    await setupTts();
    await flutterTts.speak(message);

    if (onDone != null) {
      onDone();
    }
  }

  Future<void> speakTesting(String message, {Function? onDone}) async {
    await setupTts();
    await flutterTts.speak(message);

    if (onDone != null) {
      onDone();
    }
  }

  static Future<void> setupTts() async {
    await flutterTts.setLanguage("fr-FR");
    await flutterTts.setSpeechRate(0.5);
    await flutterTts.setVolume(1.0);
    await flutterTts.setPitch(1.0);
  }
}
