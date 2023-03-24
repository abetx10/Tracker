package com.example.track.presentation.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.track.R
import com.example.track.data.LocationRepository
import com.example.track.domain.LocationTracker

class LocationService : Service() {
    private lateinit var locationTracker: LocationTracker
    private val channelId = "LocationServiceChannel"
    private val notificationId = 1

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        val locationRepository = LocationRepository(applicationContext)
        val lifecycle = ProcessLifecycleOwner.get().lifecycle
        locationTracker = LocationTracker(this, locationRepository, lifecycle)
        lifecycle.addObserver(locationTracker)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Location Service",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        locationTracker.startTracking()

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Location Service")
            .setContentText("Tracking location in the background")
            .setSmallIcon(R.drawable.ic_location)
            .build()

        startForeground(notificationId, notification)

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        locationTracker.stopTracking()
    }
}