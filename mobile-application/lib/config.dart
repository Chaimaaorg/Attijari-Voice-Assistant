class Config {
  static final app_icon = "assets/images/logo.png";
  static final apikey_twitter = "ogce2lnRHUJDcyzcwbGHnBPpr";
  static final secretkey_twitter =
      "Drnm8c5WnG4VpG8sSsQJlZ1UsOowFuwZglfsBLI1BkUMxGAzyc";
  // sign in with twitter method in sign_in_provider
  // final twitterLogin = TwitterLogin(
  //     apiKey: Config.apikey_twitter,
  //     apiSecretKey: Config.secretkey_twitter,
  //     redirectURI: "socialauth://");
  // Future signInWithTwitter() async {
  //   final authResult = await twitterLogin.loginV2();
  //   if (authResult.status == TwitterLoginStatus.loggedIn) {
  //     try {
  //       print(authResult.authTokenSecret);
  //       final credential = TwitterAuthProvider.credential(
  //           accessToken: authResult.authToken!,
  //           secret: authResult.authTokenSecret!);
  //       await firebaseAuth.signInWithCredential(credential);
  //
  //       final userDetails = authResult.user;
  //       // save all the data
  //       _name = userDetails!.name;
  //       _email = firebaseAuth.currentUser!.email;
  //       _imageUrl = userDetails.thumbnailImage;
  //       _uid = userDetails.id.toString();
  //       _provider = "TWITTER";
  //       _hasError = false;
  //       notifyListeners();
  //     } on FirebaseAuthException catch (e) {
  //       switch (e.code) {
  //         case "account-exists-with-different-credential":
  //           _errorCode =
  //           "You already have an account with us. Use correct provider";
  //           _hasError = true;
  //           notifyListeners();
  //           break;
  //
  //         case "null":
  //           _errorCode = "Some unexpected error while trying to sign in";
  //           _hasError = true;
  //           notifyListeners();
  //           break;
  //         default:
  //           _errorCode = e.toString();
  //           _hasError = true;
  //           notifyListeners();
  //       }
  //     }
  //   } else {
  //     _hasError = true;
  //     notifyListeners();
  //   }
  // }

  //=========================================//
  //method auth_controller
  // Future handleTwitterAuth(BuildContext context) async {
  //   final sp = context.read<SignInProvider>();
  //   final ip = context.read<InternetProvider>();
  //   await ip.checkInternetConnection();
  //
  //   if (ip.hasInternet == false) {
  //     openSnackbar(context, "Check your Internet connection", Colors.red);
  //     // googleController.reset();
  //   } else {
  //     await sp.signInWithTwitter().then((value) {
  //       if (sp.hasError == true) {
  //         openSnackbar(context, sp.errorCode.toString(), Colors.red);
  //         // twitterController.reset();
  //       } else {
  //         // checking whether user exists or not
  //         sp.checkUserExists().then((value) async {
  //           if (value == true) {
  //             // user exists
  //             await sp.getUserDataFromFirestore(sp.uid).then((value) =>
  //                 sp
  //                     .saveDataToSharedPreferences()
  //                     .then((value) =>
  //                     sp.setSignIn().then((value) {
  //                       // twitterController.success();
  //                       handleAfterSignIn(context);
  //                     })));
  //           } else {
  //             // user does not exist
  //             sp.saveDataToFirestore().then((value) =>
  //                 sp
  //                     .saveDataToSharedPreferences()
  //                     .then((value) =>
  //                     sp.setSignIn().then((value) {
  //                       // twitterController.success();
  //                       handleAfterSignIn(context);
  //                     })));
  //           }
  //         });
  //       }
  //     });
  //   }
  // }
  // SocalCard(
  // icon: "assets/icons/twitter.svg",
  // press: () {AuthController().handleTwitterAuth(context);},
  // ),

}
