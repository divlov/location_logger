import 'package:get/get.dart';
import 'package:flutter/services.dart';
import 'package:permission_handler/permission_handler.dart';

class LocationController extends GetxController {
  final MethodChannel _channel = const MethodChannel('location_channel');
  String _lastLocation = '';

  @override
  void onInit() {
    super.onInit();
    startBackgroundService();
    _channel.setMethodCallHandler((call) async {
      switch (call.method) {
        case 'onLocationUpdate':
          _lastLocation = call.arguments['location'];
          update();
          break;
      }
    });
  }

  Future<void> startBackgroundService() async {
    await Permission.location.request();
    await Permission.locationAlways.request();
    if (await Permission.locationAlways.request().isGranted) {
      var result=await _channel.invokeMethod('getLastLocation');
      if(result!=null){
        _lastLocation=result.toString();
        update();
      }
      result=await _channel.invokeMethod('startLocationService');
    }
  }

  String get lastLocation => _lastLocation;
}