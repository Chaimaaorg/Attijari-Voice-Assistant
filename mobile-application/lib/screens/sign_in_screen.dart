import 'package:flutter/material.dart';
import 'package:speech_to_text/speech_to_text.dart';
import 'package:test_rv/constants.dart';
import 'package:test_rv/screens/biometrics_screen.dart';
import 'package:test_rv/utils/text_to_voice.dart';
import '../components/description.dart';
import '../components/logo_and_user_prompt.dart';
import '../components/mic_button.dart';
import '../providers/auth_provider.dart';
import 'package:provider/provider.dart';
import 'dialog_screen.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:flutter_svg/svg.dart';

class SignInScreen extends StatefulWidget {
  const SignInScreen({super.key});

  static String routeName = "/SignInScreen";

  @override
  State<SignInScreen> createState() => _SignInScreenState();
}

class _SignInScreenState extends State<SignInScreen> {
  final SpeechToText _speechToText = SpeechToText();

  bool _speechEnabled = false;
  bool _firstopen = true;
  String _wordsSpoken = "";
  double _confidenceLevel = 0;

  int _currentQuestionIndex = 0;
  List<String> _questions = [
    "Merci de donner votre nom d'utilisateur.",
    "Merci de donner votre mot de passe.",
    "Voulez-vous enregistrer le login et mot de passe pour les prochaines connexions?"
  ];
  List<String> _answers = [];

  int _failedAttempts = 0;
  int _maxAttempts = 3;

  @override
  void initState() {
    super.initState();
    _initSpeech();
  }

  void initSpeech() async {
    _speechEnabled = await _speechToText.initialize();
    setState(() {});
  }

  Future<void> _initSpeech() async {
    bool hasPermission = await _speechToText.hasPermission;
    initSpeech();
    if (hasPermission) {
      if (_firstopen) {
        await TextToVoice.speak(
            "Authentification réussie. Appuyer sur l'écran pour activer ou désactiver le micro");
        _firstopen = false;
        await Future.delayed(Duration(seconds: 8));
      }
      if(_currentQuestionIndex !=_questions.length -1 ){
        TextToVoice.speak(_questions[_currentQuestionIndex]);
      }
    } else {
      TextToVoice.speak(
          "Authentification réussie. Vous n'avez pas encore autorisé l'accès au microphone. Veuillez autoriser l'accès au microphone en appuyant sur Autoriser au milieu de l'écran pour intéragir avec moi!");
    }
  }

  Future<void> _startListening() async {
    await _speechToText.listen(onResult: _onSpeechResult);
    setState(() {
      _confidenceLevel = 0;
    });
  }

  Future<void> _stopListening() async {
    await _speechToText.stop();
    setState(() {});
  }

  void _onSpeechResult(result) async {
    setState(() {
      _wordsSpoken = "${result.recognizedWords}";
      _confidenceLevel = result.confidence;
    });
    if (_speechToText.isNotListening) {
      if (_wordsSpoken == result.recognizedWords) {
        if (_wordsSpoken.isNotEmpty) {
          _answers.add(_wordsSpoken);
        }
        _currentQuestionIndex++;
        if (_currentQuestionIndex < _questions.length -1) {
          TextToVoice.speak(_questions[_currentQuestionIndex]);
        } else {
          _verifyCredentials();
        }
      }
    }
  }

  void _verifyCredentials() async {
    if (_answers.length >= _questions.length -1) {
      final username = _answers[0].toLowerCase();
      final password = _answers[1].toLowerCase();

      final authProvider = Provider.of<AuthProvider>(context, listen: false);

      final currentUser = await authProvider.login(username, password);

      if (currentUser !=null) {
        print(currentUser!.userId);
        await TextToVoice.speak(_questions[2]);
        await Future.delayed(const Duration(seconds: 3));

        if (_answers.length > _questions.length - 1) {
          if (_answers[_questions.length - 1].toLowerCase().contains("oui")) {
            await authProvider.saveCredentials(username, password,currentUser.userId);
          }
          await TextToVoice.speak(
            "Authentification réussie. Redirection vers l'écran suivant",
          );
          await Future.delayed(Duration(seconds: 3));
          Navigator.pushNamed(context, DialogScreen.routeName);
          _resetState();
        }
      } else {
        _failedAttempts++;
        if (_failedAttempts >= _maxAttempts) {
          await TextToVoice.speak(
            "Nombre maximal de tentatives échouées atteint.",
          );
          await Future.delayed(Duration(seconds: 3));
          Navigator.pushNamed(context, BiometricsScreen.routeName);
        } else {
          await TextToVoice.speak(
            "Identifiants incorrects. Tentative ${_failedAttempts} sur ${_maxAttempts}.Réessayez!",
          );
          _resetState();
        }
      }
    }
  }



  void _resetState() {
    _currentQuestionIndex = 0;
    _answers.clear();
    _wordsSpoken = "";
    _confidenceLevel = 0;
  }

  @override
  Widget build(BuildContext context) {
    final Size size = MediaQuery.of(context).size;
    return GestureDetector(
      onTap: _speechToText.isListening ? _stopListening : _startListening,
      child: Scaffold(
        // each product have a color
        backgroundColor: Colors.black,
        appBar: AppBar(
          backgroundColor: Colors.black,
          elevation: 0,
          leading: IconButton(
            icon: SvgPicture.asset(
              "assets/icons/Back Icon.svg",
              colorFilter: ColorFilter.mode(Colors.white, BlendMode.srcIn),
            ),
            onPressed: () => Navigator.pop(context),
          ),
          title: const Text(
            "Page d'authentification",
            textAlign: TextAlign.end,
            style: TextStyle(color: Colors.white),
          ),
        ),
        body: SingleChildScrollView(
          child: Column(
            children: <Widget>[
              SizedBox(
                height: size.height,
                child: Stack(
                  children: <Widget>[
                    Container(
                      margin: EdgeInsets.only(top: size.height * 0.3),
                      padding: EdgeInsets.only(
                        top: size.height * 0.12,
                        left: kDefaultPaddin,
                        right: kDefaultPaddin,
                      ),
                      // height: 500,
                      decoration: const BoxDecoration(
                        gradient: kPrimaryGradientColor,
                        borderRadius: BorderRadius.only(
                          topLeft: Radius.circular(24),
                          topRight: Radius.circular(24),
                        ),
                      ),
                      child: Column(
                        children: <Widget>[
                          Description(generatedContent: null),
                          const SizedBox(height: kDefaultPaddin / 2),
                          const SizedBox(height: kDefaultPaddin / 8),
                          MicButton(isListening: _speechToText.isListening, isNotListening: _speechToText.isNotListening, startListening: _startListening, stopListening: _stopListening),
                          if (!_speechToText.isListening && _confidenceLevel > 0)
                            Padding(
                              padding: const EdgeInsets.only(
                                bottom: 100,
                              ),
                              child: Text(
                                "Confidence: ${(_confidenceLevel * 100).toStringAsFixed(1)}%",
                                style: const TextStyle(
                                  fontSize: 30,
                                  color: Colors.blueGrey,
                                  fontWeight: FontWeight.w300,
                                ),
                              ),
                            )
                        ],
                      ),
                    ),
                    LogoAndUserPrompt(isListening: _speechToText.isListening, isAvailable: _speechToText.isAvailable, lastWords: _wordsSpoken)
                  ],
                ),
              )
            ],
          ),
        ),
      ),
    );
  }
}
