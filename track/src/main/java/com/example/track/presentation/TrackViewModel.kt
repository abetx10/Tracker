package com.example.track.presentation

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.track.data.LocationRepository
import com.example.track.data.WorkerScheduler
import com.example.track.domain.LocationTracker
import com.example.track.presentation.service.LocationService
import kotlinx.coroutines.launch

class TrackViewModel(private val context: Context) : ViewModel() {

    private val locationRepository = LocationRepository(context)
    private var locationTracker: LocationTracker? = null
    private val sharedPreferences =
        context.getSharedPreferences("track_preferences", Context.MODE_PRIVATE)

    val isTracking = MutableLiveData<Boolean>()

    init {
        isTracking.value = false
    }

    fun setLocationTracker(locationTracker: LocationTracker) {
        this.locationTracker = locationTracker
    }

    fun startTracking() {
        locationTracker?.startTracking()
        viewModelScope.launch {
            WorkerScheduler.scheduleSendLocationWorker(context)
        }
        startService()
        isTracking.value = true
        saveTrackingState(true)
    }

    fun stopTracking() {
        locationTracker?.stopTracking()
        stopService()
        isTracking.value = false
        saveTrackingState(false)
    }

    private fun startService() {
        val serviceIntent = Intent(context, LocationService::class.java)
        ContextCompat.startForegroundService(context, serviceIntent)
    }

    private fun stopService() {
        val serviceIntent = Intent(context, LocationService::class.java)
        context.stopService(serviceIntent)
    }

    private fun saveTrackingState(isTracking: Boolean) {
        sharedPreferences.edit().putBoolean("is_tracking", isTracking).apply()
    }

    fun loadTrackingState(): Boolean {
        return sharedPreferences.getBoolean("is_tracking", false)
    }

    fun startOrStopService() {
        if (isTracking.value == true) {
            startService()
        } else {
            stopService()
        }
    }
}