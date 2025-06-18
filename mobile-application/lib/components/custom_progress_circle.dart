import 'package:flutter/material.dart';
import 'package:simple_circular_progress_bar/simple_circular_progress_bar.dart';

class CustomProgressCircle extends StatelessWidget {
  final double progress;
  const CustomProgressCircle({super.key, required this.progress});

  @override
  Widget build(BuildContext context) {
    return Column(
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        SimpleCircularProgressBar(
          maxValue: progress,
          // fullProgressColor: Colors.blue,
          progressColors: [Colors.blueGrey,Colors.blue],
          size: 100,
          backStrokeWidth: 10,
        ),
        const SizedBox(height: 16),
        Text(
          '${(progress * 100).toInt()}%',
          style: const TextStyle(
            fontSize: 18,
            fontWeight: FontWeight.bold,
          ),
        ),
      ],
    );
  }
}