import 'package:test_rv/size_config.dart';
import 'package:flutter/material.dart';
//Colors for the notifs
const Color Secondary = Color(0xfff4b41e);
const Color mainText = Color(0xFF2E3E5C);
const Color SecondaryText = Color(0xFF9FA5C0);
const Color outline = Color(0xFFD0DBEA);
const Color form = Color(0xFFF4F5F7);
const Color bluePanel = Color(0xFF617BAF);
//Main colors
const kPrimaryColor = Color(0xFFe75540);
const kPaletteColor = Color(0xFFb76265);
const kDotsColor = Color(0xFF7AA5EA);
const kBeautifulBlue = Color(0xFF3D82AE);
const kPrimaryLightColor = Color(0xFFFFECDF);
const kBgColor = Color(0xFFF4F4F4);
const kPrimaryGradientColor = LinearGradient(
  begin: Alignment.topLeft,
  end: Alignment.bottomRight,
  colors: [Color(0xfff4b41e),Color(0xfff4b41e), Color(0xffe75540),Color(0xffe4664b)],
);
const kSecondaryGradientColor = LinearGradient(
  begin: Alignment.topLeft,
  end: Alignment.bottomRight,
  colors: [Color(0xFF2E3E5C),Color(0xFF3D82AE), Color(0xFF9FA5C0),Color(0xFF617BAF)],
);

const kSecondaryColor = Color(0xFF979797);
const kTextColor = Color(0xFF757575);
const kTextLightColor = Color(0xFFACACAC);


const kDefaultPaddin = 20.0;
const kAnimationDuration = Duration(milliseconds: 200);

final headingStyle = TextStyle(
  fontSize: getProportionateScreenWidth(28),
  fontWeight: FontWeight.bold,
  color: Colors.black,
  height: 1.5,
);

const defaultDuration = Duration(milliseconds: 250);


final otpInputDecoration = InputDecoration(
  contentPadding:
  EdgeInsets.symmetric(vertical: getProportionateScreenWidth(15)),
  border: outlineInputBorder(),
  focusedBorder: outlineInputBorder(),
  enabledBorder: outlineInputBorder(),
);

OutlineInputBorder outlineInputBorder() {
  return OutlineInputBorder(
    borderRadius: BorderRadius.circular(getProportionateScreenWidth(15)),
    borderSide: BorderSide(color: kTextColor),
  );
}
