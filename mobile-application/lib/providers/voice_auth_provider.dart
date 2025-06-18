import 'dart:async';

import 'package:flutter/material.dart';
import 'package:test_rv/services/voice_biometric_service.dart';
import '../models/utilisateur.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'dart:io';

class VoiceAuthProvider extends ChangeNotifier {
  final VoiceBiometricService _authService;
  final SharedPreferences _prefs;
  Utilisateur? _currentUser;
  VoiceAuthProvider(this._authService, this._prefs);

  Utilisateur? get currentUser => _currentUser;

  Future<Utilisateur?> login(File audio) async {
    final userInfo = await _authService.authenticateUserByVoicePrint(audio);
    if (userInfo != null) {
      _currentUser = Utilisateur.fromMap(userInfo);
      notifyListeners();
      return _currentUser;
    } else {
      return null;
    }
  }

  Future<void> saveCredentials(int userid) async {
    await _prefs.setInt('userId', userid);
  }

  Future<void> logout() async {
    _currentUser = null;
    notifyListeners();
    await clearCredentials();
  }

  Future<void> clearCredentials() async {
    SharedPreferences _prefs = await SharedPreferences.getInstance();
    await _prefs.remove('userId');
  }

}

