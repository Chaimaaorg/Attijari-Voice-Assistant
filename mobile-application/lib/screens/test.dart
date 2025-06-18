// import 'dart:convert';
// import 'package:flutter/material.dart';
// import 'package:percent_indicator/circular_percent_indicator.dart';
// import 'package:test_rv/constants.dart';
// import 'package:avatar_glow/avatar_glow.dart';
// import 'package:flutter_sound/flutter_sound.dart';
// import 'package:path_provider/path_provider.dart';
// import 'package:permission_handler/permission_handler.dart';
// import 'package:flutter_svg/flutter_svg.dart';
// import 'package:http/http.dart' as http;
// import 'package:http_parser/http_parser.dart';
// import 'package:test_rv/providers/voice_auth_provider.dart';
// import 'package:provider/provider.dart';
// import '../utils/text_to_voice.dart';
// import 'dialog_screen.dart';
//
// class VoiceSignInScreen extends StatefulWidget {
//   static String routeName = '/test';
//
//   const VoiceSignInScreen({Key? key}) : super(key: key);
//
//   @override
//   _VoiceSignInScreenState createState() => _VoiceSignInScreenState();
// }
//
// class _VoiceSignInScreenState extends State<VoiceSignInScreen> {
//   double _progress = 0.0; // Initial progress value
//   final recorder = FlutterSoundRecorder();
//   bool isRecording = false;
//   String? recordedFilePath;
//   int _audioCounter = 0; // Counter for the number of audios recorded
//   List<String> audioFiles = []; // List to store audio file paths
//
//   void _incrementProgress() {
//     setState(() {
//       _progress += 1 / 3; // Increment progress by 1/3
//       _audioCounter++; // Increment the audio counter
//
//       if (recordedFilePath != null) {
//         audioFiles.add(recordedFilePath!); // Add the recorded file path to the list
//       }
//
//       if (_audioCounter == 3) {
//         _progress = 0.0; // Reset progress to 0 after recording 3 audios
//         sendAudioFiles(); // Send audio files to the service
//       }
//     });
//   }
//
//   Future<void> sendAudioFiles() async {
//     final url = 'http://192.168.100.35:8002/'; // Replace with your FastAPI server URL
//     final authProvider = Provider.of<VoiceAuthProvider>(context, listen: false);
//     var request = http.MultipartRequest('POST', Uri.parse(url));
//
//     for (int i = 0; i < audioFiles.length; i++) {
//       request.files.add(await http.MultipartFile.fromPath(
//         'user_audio${i + 1}',
//         audioFiles[i],
//         contentType: MediaType('audio', 'wav'),  // Use MediaType from http_parser
//       ));
//     }
//
//     var response = await request.send();
//
//     if (response.statusCode == 200) {
//       // Parse the JSON response
//       var responseData = await response.stream.bytesToString();
//       var data = json.decode(responseData);
//
//       // Extract the mean arrays
//       List<double> meanArrays = List<double>.from(data['mean_arrays'][0]);
//       print(meanArrays);
//       final currentUser = await authProvider.login(meanArrays);
//       if(currentUser !=null){
//         print(currentUser.userId);
//         await authProvider.saveCredentials(currentUser.userId);
//         await TextToVoice.speak(
//           "Authentification réussie. Redirection vers l'écran suivant",
//         );
//         await Future.delayed(Duration(seconds: 3));
//         Navigator.pushNamed(context, DialogScreen.routeName);
//       }
//       else{
//         TextToVoice.speak("Désolé , vous n'êtes pas autorisé à utiliser ce service");
//       }
//     } else {
//       print('Failed to send audio files: ${response.statusCode}');
//     }
//
//     audioFiles.clear(); // Clear the list after sending
//   }
//
//   @override
//   void initState() {
//     super.initState();
//     TextToVoice.speak("Pour vous authentifier, appuyer sur l'écran pour enregistrer trois audios où vous répéter des phrases de votre choix!");
//     initRecorder();
//   }
//
//   @override
//   void dispose() {
//     recorder.closeRecorder();
//     super.dispose();
//   }
//
//   Future<void> initRecorder() async {
//     final status = await Permission.microphone.request();
//     if (status != PermissionStatus.granted) {
//       throw 'Microphone permission not granted';
//     }
//     await recorder.openRecorder();
//   }
//
//   Future<void> startRecording() async {
//     final directory = await getTemporaryDirectory();
//     final filePath = '${directory.path}/recording_${DateTime.now().millisecondsSinceEpoch}.wav';
//     recordedFilePath = filePath;
//     await recorder.startRecorder(
//       toFile: filePath,
//       codec: Codec.pcm16WAV,
//     );
//     setState(() {
//       isRecording = true;
//     });
//   }
//
//   Future<void> stopRecording() async {
//     await recorder.stopRecorder();
//     setState(() {
//       isRecording = false;
//       _incrementProgress(); // Increment progress after recording an audio
//     });
//   }
//
//   @override
//   Widget build(BuildContext context) {
//     return Scaffold(
//       backgroundColor: Colors.transparent, // Set the background color to transparent
//       body: Container(
//         decoration: BoxDecoration(
//           gradient: LinearGradient(
//             begin: Alignment.topLeft,
//             end: Alignment.bottomRight,
//             colors: [
//               Color(0xfff4b41e),
//               Color(0xFFB8C7CB),
//               Colors.white,
//             ],
//           ),
//         ),
//         child: Center(
//           child: Padding(
//             padding: const EdgeInsets.all(15.0),
//             child: Column(
//               mainAxisAlignment: MainAxisAlignment.spaceEvenly,
//               children: [
//                 CircularPercentIndicator(
//                   header:Hero(
//                     tag: 1,
//                     child: Image.asset(
//                       "assets/images/attijariwafa-bank-logo.png",
//                       width: 450,
//                       height: 300,
//                     ),
//                   ),
//                   backgroundColor: Colors.black87,
//                   animation: true,
//                   animationDuration: 1000,
//                   // Animation duration in milliseconds
//                   radius: 130,
//                   lineWidth: 20,
//                   percent: _progress,
//                   // Use the state variable for progress
//                   circularStrokeCap: CircularStrokeCap.round,
//                   linearGradient: kPrimaryGradientColor,
//                   center: Text(
//                     _audioCounter.toString(),
//                     style: TextStyle(color: Colors.black, fontSize: 60),
//                   ),
//                 ),
//                 AvatarGlow(
//                   startDelay: const Duration(milliseconds: 1000),
//                   glowColor: Color(0xfff4b41e),
//                   glowShape: BoxShape.circle,
//                   animate: isRecording,
//                   curve: Curves.fastOutSlowIn,
//                   child: SizedBox(
//                     width: 100,
//                     height: 100,
//                     child: FloatingActionButton(
//                       shape: const CircleBorder(),
//                       onPressed: isRecording ? stopRecording : startRecording,
//                       tooltip: 'Record',
//                       // backgroundColor: Color(0xFFB8C7CB),
//                       backgroundColor: Colors.black,
//                       child: !isRecording
//                           ? const Icon(Icons.mic_off,
//                           color: Colors.white, size: 50)
//                           : SvgPicture.asset(
//                         "assets/icons/mic.svg",
//                         colorFilter: const ColorFilter.mode(
//                             Colors.white, BlendMode.srcIn),
//                         width: 50,
//                         height: 50,
//                       ),
//                     ),
//                   ),
//                 ),
//               ],
//             ),
//           ),
//         ),
//       ),
//     );
//   }
// }