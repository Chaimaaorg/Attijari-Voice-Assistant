import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';

import '../../../constants.dart';

class LogoAndUserPrompt extends StatelessWidget {
  const LogoAndUserPrompt({super.key, required this.isListening, required this.isAvailable, required this.lastWords, });

  final bool isListening;
  final bool isAvailable;
  final String lastWords;
  @override
  Widget build(BuildContext context) {
    return                     Padding(
      padding:
      const EdgeInsets.symmetric(horizontal: kDefaultPaddin),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: <Widget>[
          Text(
            isListening
                ? "Je vous écoute..."
                : isAvailable
                ? "Appuyez sur le microphone pour commencer à parler..."
                : "Speech non disponible",
            style: TextStyle(color: Colors.white),
          ),
          Text(
            lastWords,
            style: Theme.of(context)
                .textTheme
                .titleLarge!
                .copyWith(
                color: Colors.white,
                fontWeight: FontWeight.bold),
          ),
          SizedBox(height: kDefaultPaddin),
          Row(
            children: <Widget>[
              SizedBox(width: kDefaultPaddin*5),
              Expanded(
                child: Hero(
                  tag: 1,
                  child: Padding(
                    padding: const EdgeInsets.only(left: 60),
                    child: Transform(
                      transform: Matrix4.identity()
                        ..setEntry(3, 2, 0.001) // Perspective
                        ..rotateX(0.05) // Rotate around X-axis
                        ..rotateY(0.5) // Rotate around Y-axis
                        ..rotateZ(0.07), // Rotate around Z-axis
                      alignment: FractionalOffset.center,
                      child: SvgPicture.asset(
                        "assets/icons/banknote.svg",
                        width: 450,
                        height: 300,
                        color: Colors.blueGrey,
                      ),
                    ),
                  ),
                ),
              )
            ],
          )
        ],
      ),
    )
    ;
  }
}