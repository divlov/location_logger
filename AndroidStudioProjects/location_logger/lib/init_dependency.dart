import 'package:get/get.dart';
import 'package:location_logger/screens/home/controller.dart';

class InitDep implements Bindings{
  @override
  void dependencies() {
    Get.lazyPut(() => LocationController());
  }

}