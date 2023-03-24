package com.example.track.domain

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import com.example.track.data.LocationRepository
import com.example.track.data.LocationUpdate
import com.example.track.data.NetworkUtils
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class LocationTracker(
    private val appContext: Context,
    private val locationRepository: LocationRepository,
    private val lifecycle: Lifecycle
) : DefaultLifecycleObserver {

//    private val locationRequest: LocationRequest =
//        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, TimeUnit.MINUTES.toMillis(10))
//            .setWaitForAccurateLocation(false)
//            .setMinUpdateIntervalMillis(TimeUnit.MINUTES.toMillis(10))
//            .setMaxUpdateDelayMillis(TimeUnit.MINUTES.toMillis(10))
//            .build()

    private val locationRequest: LocationRequest =
        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, TimeUnit.SECONDS.toMillis(10))
            .setMinUpdateIntervalMillis(TimeUnit.SECONDS.toMillis(10))
            .setMaxUpdateDelayMillis(TimeUnit.SECONDS.toMillis(10))
            .build()

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(appContext)

    private var currentLocation: Location? = null

    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            CoroutineScope(Dispatchers.Main).launch {
                val location = locationResult.lastLocation
                val user = FirebaseAuth.getInstance().currentUser

                if (user != null) {
                    val data = hashMapOf(
                        "latitude" to location!!.latitude,
                        "longitude" to location.longitude,
                        "timestamp" to System.currentTimeMillis()
                    )

                    if (NetworkUtils.isInternetAvailable(appContext)) {
                        FirebaseFirestore.getInstance()
                            .collection("locations")
                            .document(user.uid)
                            .set(data)
                    } else {
                        val locationUpdate = LocationUpdate(
                            id = user.uid,
                            latitude = location!!.latitude,
                            longitude = location.longitude,
                            timestamp = System.currentTimeMillis()
                        )
                        withContext(Dispatchers.IO) {
                            locationRepository.insertLocationUpdate(locationUpdate)
                        }
                    }
                    currentLocation = location
                    Toast.makeText(
                        appContext,
                        "Current location: ${location.latitude}, ${location.longitude}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    init {
        lifecycle.addObserver(this)
    }

    companion object {
        private const val REQUEST_LOCATION_PERMISSIONS = 100
    }

    private fun requestLocationPermissions() {

        if (appContext is Activity) {
            ActivityCompat.requestPermissions(
                appContext,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                REQUEST_LOCATION_PERMISSIONS
            )
        }
    }


    @SuppressLint("MissingPermission")
    fun startTracking() {
        if (ContextCompat.checkSelfPermission(
                appContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                appContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermissions()
        } else {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    fun getLastLocation(): Location? {
        return currentLocation
    }

    fun stopTracking() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}