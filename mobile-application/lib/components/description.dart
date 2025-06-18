import 'package:flutter/material.dart';

import '../../../../../../../constants.dart';

class Description extends StatelessWidget {
  const Description({
    Key? key,
    this.generatedContent,
  }) : super(key: key);

  final String? generatedContent;

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: kDefaultPaddin / 2),
      child: Text(
        generatedContent ?? "Bienvenue chez Attijariwafa Bank, où votre avenir et vos ambitions sont au cœur de nos préoccupations, parce que croire en vous et soutenir chaque étape de votre parcours financier est notre engagement le plus sincère.",
        style: const TextStyle(height: 1.5, fontSize: 15),
      ),
    );
  }
}
