import 'package:flutter/material.dart';
import 'package:local_auth/local_auth.dart';
import 'package:test_rv/utils/text_to_voice.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../screens/dialog_screen.dart';
import '../screens/sign_in_screen.dart';


class Authentication {
  static final LocalAuthentication _auth = LocalAuthentication();

  // Vérifie si le dispositif supporte l'authentification biométrique
  static Future<bool> canAuthenticate() async =>
      await _auth.canCheckBiometrics && await _auth.isDeviceSupported();

  static Future<bool> authenticate(BuildContext context) async {
    if (!await canAuthenticate()) {
      return _handleUnsupportedDevice(context);
    }
    return _authenticate(context);
  }
  Future<bool> authenticateForTesting(BuildContext context) async {
    if (!await canAuthenticate()) {
      return _handleUnsupportedDevice(context);
    }
    return _authenticate(context);
  }


  static Future<bool> _authenticate(BuildContext context) async {
    try {
      bool isAuthenticated = await _auth.authenticate(
        localizedReason:
        "Veuillez placer votre doigt sur le capteur d'empreintes digitales",
      );

      if (isAuthenticated) {
        TextToVoice.speak("Authentification réussie. Accès autorisé à l'application.");
      } else {
        TextToVoice.speak("Échec de l'authentification. Veuillez réessayer.");
      }
      return isAuthenticated;
    } catch (e) {
      print("Authentication error: $e");
      TextToVoice.speak("Une erreur s'est produite lors de l'authentification. Veuillez réessayer.");
      return false;
    }
  }

  static Future<bool> _handleUnsupportedDevice(BuildContext context) async {
    TextToVoice.speak("Votre appareil ne prend pas en charge l'authentification par empreinte digitale.");
    SharedPreferences prefs = await SharedPreferences.getInstance();
    int? userId = prefs.getInt('userId');
    Navigator.pushNamed(
      context,
      userId != null ? DialogScreen.routeName : SignInScreen.routeName,
    );
    return false;
  }

  static Future<void> _showPinDialog(BuildContext context) async {
    String enteredPin = '';

    // Afficher le dialogue demandant le code PIN
    return showDialog<void>(
      context: context,
      barrierDismissible: false,
      // l'utilisateur doit appuyer sur un bouton pour fermer le dialogue
      builder: (BuildContext context) {
        return AlertDialog(
          title: Text('Entrez votre code PIN'),
          content: SingleChildScrollView(
            child: ListBody(
              children: <Widget>[
                Text("Entrez votre code PIN pour accéder à l'application."),
                // Champ de saisie de code PIN
                TextField(
                  onChanged: (value) {
                    enteredPin = value;
                  },
                  obscureText: true, // Le texte entré est masqué
                  keyboardType: TextInputType.number, // Clavier numérique
                ),
              ],
            ),
          ),
          actions: <Widget>[
            TextButton(
              child: Text('Valider'),
              onPressed: () {
                // Vérification du code PIN
                if (enteredPin == '1234') {
                  TextToVoice.speak(
                      "Code PIN correct. Accès autorisé à l'application.");
                  Navigator.of(context).pop();
                } else {
                  TextToVoice.speak("Code PIN incorrect. Veuillez réessayer.");
                  enteredPin = ''; // Réinitialiser le code PIN entré
                }
              },
            ),
          ],
        );
      },
    );
  }
}
