import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:location_logger/screens/home/controller.dart';

class Home extends StatelessWidget {

  LocationController controller = Get.put(LocationController());

  Home({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Location')),
      body: Center(child: GetBuilder<LocationController>(builder: (_) {
        return Text(controller.lastLocation);
      })),
    );
  }
}
