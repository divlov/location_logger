package com.example.location_logger

import android.app.ActivityManager
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.location_logger.controllers.DatabaseController
import com.example.location_logger.services.LocationService
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel


class MainActivity : FlutterActivity() {

    private val CHANNEL = "location_channel"
    private lateinit var databaseHelper: DatabaseController

    private val serviceIntent: Intent by lazy {
        Intent(this, LocationService::class.java)
    }

    companion object {
        var flutterEngineInstance: FlutterEngine? = null
    }

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        flutterEngineInstance=flutterEngine;
        databaseHelper = DatabaseController(this)


        MethodChannel(
            flutterEngine.dartExecutor.binaryMessenger,
            CHANNEL
        ).setMethodCallHandler { call, result ->
            if (call.method == "getLastLocation") {
                val lastLocation = fetchLastLocation()
                result.success(lastLocation)
            } else if (call.method == "startLocationService") {
                startLocationService()
                result.success(null)
            } else {
                result.notImplemented()
            }
        }
    }

    private fun fetchLastLocation(): String {
        val location = databaseHelper.getLastLocation()
        return location ?: "No location data available"
    }

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    private fun startLocationService() {
        if (!isMyServiceRunning(LocationService::class.java)) {
            Log.d("android_debugging","starting service");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent)
            }
            else
                startService(serviceIntent)
        }
    }
}
