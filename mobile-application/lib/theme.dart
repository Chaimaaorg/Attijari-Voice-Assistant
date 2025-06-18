import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:test_rv/screens/biometrics_screen.dart';

import 'constants.dart';

final lightTheme = ThemeData(
  brightness: Brightness.light,
  // Define other light theme properties like primary color, text styles, etc.
);

final darkTheme = ThemeData(
  brightness: Brightness.dark,
  // Define other dark theme properties like primary color, text styles, etc.
);

ThemeData theme() {
  return ThemeData(
    scaffoldBackgroundColor: Colors.white,
    fontFamily: "Muli",
    appBarTheme: AppBarTheme(
      color: Colors.white,
      elevation: 0,
      iconTheme: IconThemeData(color: Colors.black),
      systemOverlayStyle: SystemUiOverlayStyle.dark,
      toolbarTextStyle:
          TextTheme(headline6: TextStyle(color: Color(0XFFDD072A))).bodyText2,
      titleTextStyle:
          TextTheme(headline6: TextStyle(color: Color(0XFFDD072A))).headline6,
    ),
    textTheme: TextTheme(
      bodyText1: TextStyle(color: kTextColor),
      bodyText2: TextStyle(color: kTextColor),
    ),
  );
}

InputDecorationTheme inputDecorationTheme() {
  OutlineInputBorder outlineInputBorder = OutlineInputBorder(
    borderRadius: BorderRadius.circular(100),
    borderSide: BorderSide(color: kTextColor),
    gapPadding: 10,
  );
  return InputDecorationTheme(
    floatingLabelBehavior: FloatingLabelBehavior.always,
    contentPadding: EdgeInsets.symmetric(horizontal: 42, vertical: 20),
    enabledBorder: outlineInputBorder,
    focusedBorder: outlineInputBorder,
    border: outlineInputBorder,
  );
}

TextTheme textTheme() {
  return TextTheme(
    bodyText1: TextStyle(color: kTextColor),
    bodyText2: TextStyle(color: kTextColor),
  );
}

AppBarTheme appBarTheme() {
  return const AppBarTheme(
    color: Colors.white,
    elevation: 0,
    iconTheme: IconThemeData(color: Colors.black),
  );
}

AppBar buildCustomAppBar(BuildContext context, text) {
  return AppBar(
    title:  Padding(
      padding: EdgeInsets.only(left: 40.0,top: 2),
      child: Text(
        text,
        style: TextStyle(color: kPrimaryColor,fontWeight: FontWeight.bold,fontSize: 18),
      ),
    ),
    backgroundColor: Colors.white,
    elevation: 0,
    leading: Builder(
      builder: (context) => IconButton(
        icon: Icon(
          Icons.arrow_back_ios,
          color: kTextColor,
        ),
        onPressed: () {
          Navigator.pop(context);
        },
      ),
    ),
  );
}
