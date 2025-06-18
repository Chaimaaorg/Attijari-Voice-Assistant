import 'package:flutter/cupertino.dart';
import 'package:test_rv/screens/biometrics_screen.dart';
import 'package:test_rv/screens/dialog_screen.dart';
import 'package:test_rv/screens/google_maps_screen.dart';
import 'package:test_rv/screens/sign_in_screen.dart';
import 'package:test_rv/screens/voice_signin_screen.dart';

final Map<String, WidgetBuilder> routes = {
  BiometricsScreen.routeName: (context) => BiometricsScreen(),
  SignInScreen.routeName : (context) => SignInScreen(),
  DialogScreen.routeName : (context) => DialogScreen(),
  GoogleMapsScreen.routeName: (context) => GoogleMapsScreen.fromContext(context),
  VoiceSignInScreen.routeName: (context) => VoiceSignInScreen(),


};