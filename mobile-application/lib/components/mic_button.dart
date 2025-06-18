import 'package:avatar_glow/avatar_glow.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:test_rv/constants.dart';


class MicButton extends StatelessWidget {
  const MicButton({super.key, required this.isListening, required this.isNotListening, required this.startListening, required this.stopListening});

  final bool isListening;
  final bool isNotListening;
  final Future<void> Function() startListening;
  final Future<void> Function() stopListening;

  @override
  Widget build(BuildContext context) {
    return AvatarGlow(
        startDelay: const Duration(milliseconds: 1000),
        glowColor: Colors.blueGrey,
        glowShape: BoxShape.circle,
        animate: isListening,
        curve: Curves.fastOutSlowIn,
        child: SizedBox(
          width: 100,
          height: 100,
          child: FloatingActionButton(
            shape: const CircleBorder(),
            onPressed: isListening
                ? stopListening
                : startListening,
            // isListening ? stopListening : startListening,
            tooltip: 'Ecouter',
            backgroundColor: kSecondaryColor,
            child: isNotListening
                ? const Icon(Icons.mic_off,
                color: Colors.black, size: 50)
                : SvgPicture.asset(
              "assets/icons/mic.svg",
              colorFilter: const ColorFilter.mode(
                  Colors.black, BlendMode.srcIn),
              width: 50,
              height: 50,
            ),
          ),
        ));
  }
}
