package com.example.map.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.map.domain.LocationMapHandler
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng

class LocationMapViewModel : ViewModel() {
    private val _googleMap = MutableLiveData<GoogleMap>()
    val googleMap: LiveData<GoogleMap> = _googleMap

    private val _route = MutableLiveData<List<LatLng>>()
    val route: LiveData<List<LatLng>> = _route

    private val _locationMapHandler = MutableLiveData<LocationMapHandler>()
    val locationMapHandler: LiveData<LocationMapHandler> = _locationMapHandler

    fun initializeMap(map: GoogleMap, fragment: SupportMapFragment, googleMapsApiKey: String) {
        val locationMapHandler = LocationMapHandler(fragment, map, googleMapsApiKey) { newRoute ->
            updateRoute(newRoute)
        }
        _locationMapHandler.value = locationMapHandler
        _googleMap.value = locationMapHandler.getMap()
    }

    private fun updateRoute(route: List<LatLng>?, saveState: Boolean = true) {
        _route.value = route
    }
}