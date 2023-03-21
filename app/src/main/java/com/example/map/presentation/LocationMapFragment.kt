package com.example.map.presentation

import android.os.Bundle
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.example.map.domain.LocationMapHandler

class LocationMapFragment: SupportMapFragment(), OnMapReadyCallback {
    private var locationMapHandler: LocationMapHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        locationMapHandler = LocationMapHandler(this, googleMap)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        locationMapHandler?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}