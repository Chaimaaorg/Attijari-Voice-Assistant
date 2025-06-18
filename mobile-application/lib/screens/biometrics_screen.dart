import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:test_rv/size_config.dart';
import 'package:test_rv/screens/dialog_screen.dart';
import 'package:test_rv/screens/sign_in_screen.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../constants.dart';
import '../utils/authentication.dart';
import '../utils/text_to_voice.dart';

class BiometricsScreen extends StatefulWidget {
  const BiometricsScreen({super.key});

  static String routeName = "/BiometricsScreen";

  @override
  State<BiometricsScreen> createState() => _BiometricsScreenState();
}

class _BiometricsScreenState extends State<BiometricsScreen> {
  int tapCount = 0;

  @override
  void initState() {
    super.initState();
    TextToVoice.speak("Appuyer trois fois sur l'écran pour vous authentifier");
  }

  Future<void> _authenticate(BuildContext context) async {
    bool auth = await Authentication.authenticate(context);
    SharedPreferences prefs = await SharedPreferences.getInstance();
    int? userId = prefs.getInt('userId');
    if (auth) {
      if(userId !=null){
        // ignore: use_build_context_synchronously
        Navigator.pushNamed(context, DialogScreen.routeName);
      }
      else{
        // ignore: use_build_context_synchronously
        Navigator.pushNamed(context, SignInScreen.routeName);
      }
    }
  }

  void _handleTap() {
    tapCount++;
    if (tapCount == 3) {
      _authenticate(context);
      tapCount = 0; // Reset tap count
    }
  }

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: _handleTap,
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
                    "Utilisez votre empreinte digitale pour vous connecter à l'application!",
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
                    onPressed: () async => await _authenticate(context),
                    icon: const Icon(
                      Icons.fingerprint,
                      color: Colors.black,
                    ),
                    label: const Text(
                      "Authentification",
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
