import 'dart:async';

import 'package:flutter/material.dart';
import '../models/utilisateur.dart';
import '../services/auth_service.dart';

import 'package:shared_preferences/shared_preferences.dart';

class AuthProvider extends ChangeNotifier {
  final AuthService _authService;
  final SharedPreferences _prefs;
  Utilisateur? _currentUser;
  AuthProvider(this._authService, this._prefs);

  Utilisateur? get currentUser => _currentUser;

  Future<Utilisateur?> login(String username, String password) async {
    final userInfo = await _authService.getUserInfo(username, password);
    if (userInfo != null) {
      _currentUser = Utilisateur.fromMap(userInfo);
      notifyListeners();
      return _currentUser;
    } else {
      return null;
    }
  }

  Future<void> saveCredentials(String username, String password,int userid) async {
    await _prefs.setString('username', username);
    await _prefs.setInt('userId', userid);
    await _prefs.setString('password', password);
  }

  Future<void> logout() async {
    _currentUser = null;
    notifyListeners();
    await clearCredentials();
  }

  Future<void> clearCredentials() async {
    SharedPreferences _prefs = await SharedPreferences.getInstance();
    await _prefs.remove('username');
    await _prefs.remove('password');
  }

  Future<void> tryAutoLogin() async {
    SharedPreferences _prefs = await SharedPreferences.getInstance();
    final savedUsername = _prefs.getString('username');
    final savedPassword = _prefs.getString('password');
    if (savedUsername != null && savedPassword != null) {
      await login(savedUsername, savedPassword);
    }
  }
}

