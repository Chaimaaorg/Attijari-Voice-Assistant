import 'package:flutter/material.dart';
import 'package:test_rv/services/dialog_service.dart';

class DialogProvider extends ChangeNotifier {
  final DialogService _dialogService ;

  DialogProvider(this._dialogService);


  Future<Object> getDialog(String message, int userIdProvider) async {
    final response = await _dialogService.getDialog(message, userIdProvider);
    return response;
  }
}