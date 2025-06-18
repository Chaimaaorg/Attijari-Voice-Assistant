import 'package:flutter/material.dart';
import 'package:flutter_svg/svg.dart';
import 'package:flutter_typeahead/flutter_typeahead.dart';
import 'package:geocoding/geocoding.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:url_launcher/url_launcher.dart';

import '../../size_config.dart';

class GoogleMapsScreen extends StatefulWidget {
  static String routeName = "/MapsScreen";

  final String initialAddress;
  final TextEditingController? addressController;

  const GoogleMapsScreen({
    Key? key,
    required this.initialAddress,
    this.addressController,
  }) : super(key: key);

  // Utilisez cette méthode pour récupérer les arguments passés lors de la navigation
  static GoogleMapsScreen fromContext(BuildContext context) {
    final routeSettings = ModalRoute
        .of(context)
        ?.settings;
    final args = routeSettings?.arguments as Map<String, dynamic>?;

    if (args != null && args.containsKey('initialAddress')) {
      return GoogleMapsScreen(initialAddress: args['initialAddress']);
    } else {
      throw Exception(
          'Required argument "initialAddress" was not provided to GoogleMapsScreenTest.');
    }
  }

  @override
  _GoogleMapsScreenState createState() => _GoogleMapsScreenState();
}

class _GoogleMapsScreenState extends State<GoogleMapsScreen> {
  final TextEditingController _searchController = TextEditingController();
  GoogleMapController? _mapController;
  Set<Marker> _markers = {};
  List<Location> _suggestions = [];
  LatLng? _selectedLatLng;

  @override
  void initState() {
    super.initState();
    _initLocation();
  }

  @override
  void dispose() {
    _searchController.dispose();
    super.dispose();
  }

  void _initLocation() async {
    if (widget.initialAddress.isNotEmpty) {
      List<Location> locations =
      await locationFromAddress(widget.initialAddress);
      if (locations.isNotEmpty) {
        Location location = locations.first;
        _selectedLatLng = LatLng(location.latitude, location.longitude);
      }else {
        // Handle the case where no location is found
        print('No location found for the provided address');
      }
    }
    if (_selectedLatLng != null) {
      String formattedAddress =
      await _formatAddressFromLatLng(_selectedLatLng!);
      _searchController.text = formattedAddress;
    }
  }


  void _launchNavigation() async {
    if (widget.initialAddress.isNotEmpty) {
      String encodedAddress = Uri.encodeComponent(widget.initialAddress);
      String mapsUrl =
          'https://www.google.com/maps/dir/?api=1&destination=$encodedAddress';
      if (await canLaunch(mapsUrl)) {
        await launch(mapsUrl);
      } else {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text("Cannot launch the maps URL: $mapsUrl")),
        );
      }
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text("Address is empty, cannot launch navigation.")),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Stack(
        children: [
          SizedBox(
            height: MediaQuery
                .of(context)
                .size
                .height,
            width: double.infinity,
            child: GoogleMap(
              myLocationButtonEnabled: true,
              onMapCreated: _onMapCreated,
              initialCameraPosition: _selectedLatLng != null
                  ? CameraPosition(target: _selectedLatLng!, zoom: 13)
                  : const CameraPosition(
                target: LatLng(0, 0),
                zoom: 13,
              ),
              markers: _selectedLatLng != null
                  ? {
                Marker(
                  markerId: MarkerId("selectedLocation"),
                  position: _selectedLatLng!,
                ),
              }
                  : {}, // Set an empty set if _selectedLatLng is null
            ),
          ),
          Positioned(
            top: 50,
            left: 20,
            right: 20,
            child: SizedBox(
              height: 100,
              child: Row(
                children: [
                  SvgPicture.asset(
                    "assets/icons/Back Icon.svg",
                    height: 13,
                  ),
                  const SizedBox(width: 10),
                  Expanded(child: _buildSuggestionsDropdown()),
                ],
              ),
            ),
          ),
          Positioned(
            bottom: 50,
            left: 20,
            right: 20,
            child: Padding(
                padding: const EdgeInsets.symmetric(
                    vertical: 50.0, horizontal: 60.0),
                child: SizedBox(
                    width: double.infinity,
                    height: getProportionateScreenHeight(45),
                    child: Semantics(
                      label: "Démarrer l'itinéraire",
                      hint: "Appuyez sur ce bouton pour démarrer la navigation vers votre destination.",
                      enabled: true,
                      child: FloatingActionButton(
                        onPressed: _launchNavigation,
                        tooltip: 'Start Navigation',
                        backgroundColor: Colors.orangeAccent,
                        child: const Text(
                          "Démarrer l'itinéraire",
                          style: TextStyle(color: Colors.white),
                        ),
                      ),
                    ))),
          ),
        ],
      ),
    );
  }

  void _onMapCreated(GoogleMapController controller) {
    _mapController = controller;
    if (_selectedLatLng != null) {
      _mapController?.animateCamera(
          CameraUpdate.newLatLngZoom(_selectedLatLng!, 13));
      _addMarker(_selectedLatLng!);
    }
  }
  void _addMarker(LatLng position) {
    Marker marker = Marker(
      markerId: MarkerId(position.toString()),
      position: position,
      infoWindow: InfoWindow(
          title: 'Agence',
          snippet: "Ceci est l'agence",
      ),
    );

    setState(() {
      _markers.add(marker); // Ajouter le marqueur à l'ensemble
    });
  }


    void _onSearchSubmitted(String value) async {
      List<Location> locations = await locationFromAddress(value);
      if (locations.isNotEmpty) {
        Location location = locations.first;
        String formattedAddress = await _formatLocation(location);
        setState(() {
          _selectedLatLng = LatLng(location.latitude, location.longitude);
          _searchController.text = formattedAddress;
        });
        _mapController?.animateCamera(CameraUpdate.newLatLng(_selectedLatLng!));
      }
    }

    void _showSnackBar(String message) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(message)),
      );
    }

    void _onSearchChanged(String value) async {
      List<Location> locations = await locationFromAddress(value);
      setState(() {
        _suggestions = locations;
      });
    }

    Widget _buildSuggestionsDropdown() {
      return TypeAheadField<String>(
        textFieldConfiguration: TextFieldConfiguration(
          onChanged: _onSearchChanged,
          controller: _searchController,
          decoration: InputDecoration(
            filled: true,
            fillColor: Colors.transparent,
            hintText: 'Enter your location',
            suffixIcon: IconButton(
              icon: Icon(Icons.search),
              onPressed: () {
                _onSearchSubmitted(_searchController.text);
              },
            ),
            contentPadding: const EdgeInsets.only(
                left: 20, bottom: 5, right: 5),
            focusedBorder: OutlineInputBorder(
              borderRadius: BorderRadius.circular(10),
              borderSide: const BorderSide(
                color: Colors.orangeAccent,
                width: 1.5,
              ),
            ),
            enabledBorder: OutlineInputBorder(
              borderRadius: BorderRadius.circular(10),
              borderSide:
              const BorderSide(color: Colors.orangeAccent, width: 1.5),
            ),
          ),
        ),
        suggestionsCallback: (pattern) => _getFormattedSuggestions(pattern),
        itemBuilder: (context, String suggestion) {
          return ListTile(title: Text(suggestion));
        },
        onSuggestionSelected: (String selectedSuggestion) {
          _searchController.text = selectedSuggestion;
          _onSearchSubmitted(selectedSuggestion);
        },
      );
    }

    Future<List<String>> _getFormattedSuggestions(String pattern) async {
      List<Location> locations = await locationFromAddress(pattern);
      List<String> suggestions = [];

      for (var location in locations) {
        String formattedLocation = await _formatLocation(location);
        suggestions.add(formattedLocation);
      }

      return suggestions;
    }

    Future<String> _formatLocation(Location location) async {
      try {
        List<Placemark> placemarks =
        await placemarkFromCoordinates(location.latitude, location.longitude);

        if (placemarks.isNotEmpty) {
          Placemark placemark = placemarks.first;
          String address = placemark.street ?? '';
          if (placemark.subLocality != null) {
            address += ', ' + placemark.subLocality!;
          }
          if (placemark.locality != null) {
            address += ', ' + placemark.locality!;
          }
          if (placemark.administrativeArea != null) {
            address += ', ' + placemark.administrativeArea!;
          }
          if (placemark.country != null) {
            address += ', ' + placemark.country!;
          }
          return address;
        }
      } catch (e) {
        print('Error formatting location: $e');
      }

      return 'Location not found';
    }

    Future<String> _formatAddressFromLatLng(LatLng latLng) async {
      try {
        List<Placemark> placemarks =
        await placemarkFromCoordinates(latLng.latitude, latLng.longitude);

        if (placemarks.isNotEmpty) {
          Placemark placemark = placemarks.first;
          String address = placemark.street ?? '';
          if (placemark.subLocality != null) {
            address += ', ' + placemark.subLocality!;
          }
          if (placemark.locality != null) {
            address += ', ' + placemark.locality!;
          }
          if (placemark.administrativeArea != null) {
            address += ', ' + placemark.administrativeArea!;
          }
          if (placemark.country != null) {
            address += ', ' + placemark.country!;
          }
          return address;
        }
      } catch (e) {
        print('Error formatting address: $e');
      }

      return 'Address not found';
    }
  }
