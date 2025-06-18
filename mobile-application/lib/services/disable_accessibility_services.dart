import 'package:flutter/services.dart';

void disableAccessibilityServices() async {
  try {
    // Disable VoiceOver
    await SystemChrome.setEnabledSystemUIMode(
      SystemUiMode.immersiveSticky,
    );

    // Disable TalkBack
    await SystemChrome.setEnabledSystemUIMode(
      SystemUiMode.immersiveSticky,
    );
  } catch (e) {
    print('Error disabling accessibility services: $e');
  }
}