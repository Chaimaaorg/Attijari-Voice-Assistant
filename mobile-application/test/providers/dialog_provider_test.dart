import 'package:flutter_test/flutter_test.dart';
import 'package:mocktail/mocktail.dart';
import 'package:test_rv/providers/dialog_provider.dart';
import 'package:test_rv/services/dialog_service.dart';

class MockDialogService extends Mock implements DialogService {}

void main() {
  group('DialogProvider', () {
    late DialogProvider dialogProvider;
    late MockDialogService mockDialogService;

    setUp(() {
      mockDialogService = MockDialogService();
      dialogProvider = DialogProvider(mockDialogService);
    });

    test('getDialog returns response from service', () async {
      // Arrange
      const message = 'Test message';
      const userIdProvider = 123;
      final expectedResponse = 'Test response';

      when(() => mockDialogService.getDialog(message, userIdProvider))
          .thenAnswer((_) async => expectedResponse);

      // Act
      final response = await dialogProvider.getDialog(message, userIdProvider);

      // Assert
      expect(response, equals(expectedResponse));
      verify(() => mockDialogService.getDialog(message, userIdProvider)).called(1);
    });

    test('getDialog throws an error', () async {
      // Arrange
      const message = 'Test message';
      const userIdProvider = 123;
      final expectedError = 'Test error';

      when(() => mockDialogService.getDialog(message, userIdProvider))
          .thenThrow(expectedError);

      // Act & Assert
      expect(() async => await dialogProvider.getDialog(message, userIdProvider),
          throwsA(expectedError));
      verify(() => mockDialogService.getDialog(message, userIdProvider)).called(1);
    });
  });
}
