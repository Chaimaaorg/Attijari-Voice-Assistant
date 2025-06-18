import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:test_rv/screens/google_maps_screen.dart';
import 'package:test_rv/size_config.dart';

final Map<String, WidgetBuilder> mockRoutes = {
  GoogleMapsScreen.routeName: (context) => GoogleMapsScreen.fromContext(context),
};
void main() {
  testWidgets('GoogleMapsScreen launches', (WidgetTester tester) async {
    await tester.pumpWidget(MaterialApp(home: Builder(builder: (context) {
      SizeConfig.init(context);
      return ElevatedButton(
        onPressed: () {
          Navigator.pushNamed(context, GoogleMapsScreen.routeName,
              arguments: {'initialAddress': 'Casablanca'});
        },
          child: Text('Go to GoogleMapsScreen')
      );
    }),routes: mockRoutes,));

    // Verify if the ElevatedButton is rendered
    expect(find.text('Go to GoogleMapsScreen'), findsOneWidget);

    await tester.tap(find.text('Go to GoogleMapsScreen'));
    await tester.pumpAndSettle();

    // Check if the initial address is displayed
    expect(find.text('Casablanca'), findsOneWidget);
  });

  testWidgets('GoogleMapsScreen displays initial address in search controller',
      (WidgetTester tester) async {
        await tester.pumpWidget(MaterialApp(home: Builder(builder: (context) {
          SizeConfig.init(context);
          return ElevatedButton(
              onPressed: () {
                Navigator.pushNamed(context, GoogleMapsScreen.routeName,
                    arguments: {'initialAddress': 'Casablanca'});
              },
              child: Text('Go to GoogleMapsScreen')
          );
        }),routes: mockRoutes,));

    await tester.pumpAndSettle();

    // Check if the initial address is displayed in the search controller
    expect(find.text('Casablanca'), findsOneWidget);
  });

  testWidgets('GoogleMapsScreen can search for other addresses',
      (WidgetTester tester) async {
        await tester.pumpWidget(MaterialApp(home: Builder(builder: (context) {
          SizeConfig.init(context);
          return ElevatedButton(
              onPressed: () {
                Navigator.pushNamed(context, GoogleMapsScreen.routeName,
                    arguments: {'initialAddress': 'Casablanca'});
              },
              child: Text('Go to GoogleMapsScreen')
          );
        }),routes: mockRoutes,));

    await tester.pumpAndSettle();

    await tester.tap(find.byIcon(Icons.search));
    await tester.pumpAndSettle();

    await tester.enterText(find.byType(TextField), 'New Address');
    await tester.testTextInput.receiveAction(TextInputAction.done);
    await tester.pumpAndSettle();

    // Check if the new address is displayed in the search controller
    expect(find.text('New Address'), findsOneWidget);
  });

  testWidgets('GoogleMapsScreen initializes markers for initial address',
      (WidgetTester tester) async {
        await tester.pumpWidget(MaterialApp(home: Builder(builder: (context) {
          SizeConfig.init(context);
          return ElevatedButton(
              onPressed: () {
                Navigator.pushNamed(context, GoogleMapsScreen.routeName,
                    arguments: {'initialAddress': 'Casablanca'});
              },
              child: Text('Go to GoogleMapsScreen')
          );
        }),routes: mockRoutes,));

    await tester.pumpAndSettle();

    // Check if the marker is initialized for the initial address
    expect(find.byType(Marker), findsOneWidget);
  });
}
