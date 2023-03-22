package com.example.track.presentation

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.track.data.LocationRepository
import com.example.track.data.WorkerScheduler
import com.example.track.domain.LocationTracker
import kotlinx.coroutines.launch

class TrackViewModel(private val context: Context) : ViewModel() {

    private val locationRepository = LocationRepository(context)
    private var locationTracker: LocationTracker? = null

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
        isTracking.value = true
    }

    fun stopTracking() {
        locationTracker?.stopTracking()
        isTracking.value = false
    }
}