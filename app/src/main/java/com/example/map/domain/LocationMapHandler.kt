package com.example.map.domain

import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import android.Manifest
import android.graphics.Color
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.PendingResult
import com.google.maps.model.DirectionsResult
import com.google.maps.model.TravelMode
import com.example.map.data.FirebaseLocationProvider

//ok no polyline
//class LocationMapHandler(private val fragment: Fragment, private val map: GoogleMap) {
//    private val LOCATION_PERMISSION_REQUEST_CODE = 1
//
//    init {
//        enableMyLocation()
//    }
//
//    private fun enableMyLocation() {
//        if (ContextCompat.checkSelfPermission(fragment.requireContext(),
//                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            map.isMyLocationEnabled = true
//            getCurrentLocation()
//        } else {
//            ActivityCompat.requestPermissions(fragment.requireActivity(),
//                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//                LOCATION_PERMISSION_REQUEST_CODE)
//        }
//        map.uiSettings.isZoomControlsEnabled = true
//    }
//
//    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
//        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                enableMyLocation()
//            }
//        }
//    }
//
//    private fun getCurrentLocation() {
//        try {
//            val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(fragment.requireActivity())
//            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
//                location?.let {
//                    val currentLatLng = LatLng(it.latitude, it.longitude)
//                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
//                    map.addMarker(MarkerOptions().position(currentLatLng).title("Your current location"))
//                }
//            }
//        } catch (e: SecurityException) {
//
//        }
//    }
//}

class LocationMapHandler(private val fragment: Fragment, private val map: GoogleMap):
    GoogleMap.OnMyLocationButtonClickListener {
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private var polyline: Polyline? = null
    private lateinit var geoApiContext: GeoApiContext

    init {
        enableMyLocation()
        initGeoApiContext()
        map.setOnMyLocationButtonClickListener(this)
    }

    private fun initGeoApiContext() {
        geoApiContext = GeoApiContext.Builder()
            .apiKey("GOOGLE_MAPS_API_KEY")
            .build()
    }

    private fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(
                fragment.requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true
            getCurrentLocation { _ ->
                // Do nothing here
            }
        } else {
            ActivityCompat.requestPermissions(
                fragment.requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
        map.uiSettings.isZoomControlsEnabled = true
    }


    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation()
            }
        }
    }

    private fun getCurrentLocation(callback: (location: LatLng?) -> Unit) {
        try {
            val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(fragment.requireActivity())
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val currentLatLng = LatLng(it.latitude, it.longitude)
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                    map.addMarker(MarkerOptions().position(currentLatLng).title("Build route"))

                    map.setOnMarkerClickListener { marker ->
                        if (marker.title == "Build route") {
                            onGetLocationButtonClick()
                        }
                        false
                    }

                    callback(currentLatLng)
                } ?: callback(null)
            }
        } catch (e: SecurityException) {
            callback(null)
        }
    }

    private fun onGetLocationButtonClick() {
        getCurrentLocation { currentLatLng ->
            FirebaseLocationProvider().getCurrentFirebaseLocation { firebaseLocation ->
                if (currentLatLng != null && firebaseLocation != null) {
                    val firebaseLatLng =
                        LatLng(firebaseLocation.latitude, firebaseLocation.longitude)
                    drawRoute(currentLatLng, firebaseLatLng)
                }
            }
        }
    }

    private fun drawRoute(start: LatLng, end: LatLng) {
        val startGms = com.google.maps.model.LatLng(start.latitude, start.longitude)
        val endGms = com.google.maps.model.LatLng(end.latitude, end.longitude)

        val directionsApiRequest = DirectionsApi.newRequest(geoApiContext)
            .mode(TravelMode.DRIVING)
            .origin(startGms)
            .destination(endGms)

        directionsApiRequest.setCallback(object : PendingResult.Callback<DirectionsResult> {
            override fun onResult(result: DirectionsResult) {
                val path = result.routes[0].overviewPolyline.decodePath()
                val latLngPath = path.map { point -> LatLng(point.lat, point.lng) }

                fragment.requireActivity().runOnUiThread {
                    polyline?.remove()
                    polyline = map.addPolyline(
                        PolylineOptions()
                            .addAll(latLngPath)
                            .color(Color.BLUE)
                            .width(10f)
                    )
                }
            }

            override fun onFailure(e: Throwable) {
            }
        })
    }

    override fun onMyLocationButtonClick(): Boolean {
        onGetLocationButtonClick()
        return false
    }
}