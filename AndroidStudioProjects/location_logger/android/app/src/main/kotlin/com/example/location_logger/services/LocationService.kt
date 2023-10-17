package com.example.location_logger.services

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.location_logger.MainActivity
import com.example.location_logger.R
import com.example.location_logger.controllers.DatabaseController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import io.flutter.plugin.common.MethodChannel

class LocationService : Service() {

    private val fusedLocationProviderClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }
//    private val flutterEngine: FlutterEngine by lazy {
//        FlutterEngine(this)
//    }

    private val databaseController: DatabaseController by lazy {
        DatabaseController(this)
    }

    private lateinit var notificationManager:NotificationManager

    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            val location = result.lastLocation

            databaseController.insertLocation(location!!.latitude,location.longitude)

            val channel = MethodChannel(MainActivity.flutterEngineInstance!!.dartExecutor.binaryMessenger,"location_channel")
            channel.invokeMethod("onLocationUpdate", mapOf("location" to "${location.latitude},${location.longitude}"))
        }
    }

    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Log.d("android_debugging","service started")
        //102=QUALITY_BALANCED_POWER_ACCURACY
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager;
        createNotificationChannel();
        startForeground(101, notification.build());
        val locationRequest=LocationRequest.Builder(102,10000).build();
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        return super.onStartCommand(intent, flags, startId)
    }

//    @SuppressLint("MissingPermission")
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        Log.d("android_debugging","service started")
//        //102=QUALITY_BALANCED_POWER_ACCURACY
//        val locationRequest=LocationRequest.Builder(102,10000).build();
//        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
//        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
//        return START_STICKY
//    }
    override fun onDestroy() {
        super.onDestroy()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                "locationService",
                "Location use",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationChannel.description = "When location is used in background"
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    var notification: NotificationCompat.Builder =
        NotificationCompat.Builder(this, "locationService")
            .setContentTitle("Location being accessed")
            .setContentText("Your location is being accessed for UI updates.")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationManager.IMPORTANCE_LOW)
            .setOngoing(true);

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
}


