import 'package:speech_to_text/speech_to_text.dart';

class SpeechUtils {


  static void stopListening(SpeechToText speechToText, Function setStateCallback) async {
    await speechToText.stop();
    setStateCallback();
  }
}
