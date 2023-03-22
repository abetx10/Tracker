package com.example.track.domain

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import android.Manifest
import android.location.Location
import android.widget.Toast
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.track.data.LocationRepository
import com.example.track.data.LocationUpdate
import com.example.track.data.NetworkUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class LocationTracker(
    private val activity: Activity,
    lifecycle: Lifecycle,
    private val locationRepository: LocationRepository
) : DefaultLifecycleObserver {

    private val locationRequest: LocationRequest =
        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, TimeUnit.MINUTES.toMillis(10))
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(TimeUnit.MINUTES.toMillis(10))
            .setMaxUpdateDelayMillis(TimeUnit.MINUTES.toMillis(10))
            .build()

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(activity)

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

                    if (NetworkUtils.isInternetAvailable(activity)) {
                        FirebaseFirestore.getInstance()
                            .collection("locations")
                            .document(user.uid)
                            .set(data)
                    } else {
                        // Сохранить в локальной базе данных
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
                        activity,
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
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            REQUEST_LOCATION_PERMISSIONS
        )
    }

    @SuppressLint("MissingPermission")
    fun startTracking() {
        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                activity,
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

    override fun onDestroy(owner: LifecycleOwner) {
        stopTracking()
    }

    fun stopTracking() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}