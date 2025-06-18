import '../models/utilisateur.dart';
import '../providers/auth_provider.dart';

class AuthController {
  final AuthProvider _authProvider;

  AuthController(this._authProvider);

  // Method to handle login
  Future<bool> login(String username, String password) async {
    var user = await _authProvider.login(username, password);
    if (user != null) {
      await _authProvider.saveCredentials(username, password, user.userId);
      return true;
    }
    return false;
  }

  // Method to handle logout
  Future<void> logout() async {
    await _authProvider.logout();
  }

  // Method to handle automatic login on app start
  Future<bool> tryAutoLogin() async {
    await _authProvider.tryAutoLogin();
    return _authProvider.currentUser != null;
  }

  // Optionally, add methods to access current user data if needed
  Utilisateur? getCurrentUser() {
    return _authProvider.currentUser;
  }
}
