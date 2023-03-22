package com.example.map.presentation

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.map.domain.LocationMapHandler
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment

class LocationMapFragment : SupportMapFragment(), OnMapReadyCallback {
    private lateinit var viewModel: LocationMapViewModel
    private var locationMapHandler: LocationMapHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getMapAsync(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(LocationMapViewModel::class.java)

        viewModel.route.observe(viewLifecycleOwner, Observer { route ->
            locationMapHandler?.updateRoute(route)
        })
    }

    override fun onMapReady(googleMap: GoogleMap) {
        viewModel.initializeMap(googleMap, this)
        viewModel.locationMapHandler.observe(viewLifecycleOwner, Observer { locationMapHandler ->
            this.locationMapHandler = locationMapHandler
        })

        viewModel.route.value?.let { restoredRoute ->
            locationMapHandler?.updateRoute(restoredRoute)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        locationMapHandler?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}