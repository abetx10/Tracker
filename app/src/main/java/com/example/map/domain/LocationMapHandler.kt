package com.example.map.domain

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.map.data.FirebaseLocationProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.PendingResult
import com.google.maps.model.DirectionsResult
import com.google.maps.model.TravelMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class LocationMapHandler(
    private val fragment: Fragment,
    private val map: GoogleMap,
    private val onRouteUpdated: (route: List<LatLng>) -> Unit
) : GoogleMap.OnMyLocationButtonClickListener {

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
            .apiKey("GOOGLE_API_KEY")
            .build()
    }

    private fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(
                fragment.requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true
            CoroutineScope(Dispatchers.Main).launch {
                val currentLatLng = getCurrentLocation()
                currentLatLng?.let {
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 15f))
                }
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

    private suspend fun getCurrentLocation(): LatLng? = withContext(Dispatchers.IO) {
        try {
            val fusedLocationClient: FusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(fragment.requireActivity())
            val location = fusedLocationClient.lastLocation.await()
            location?.let {
                LatLng(it.latitude, it.longitude)
            }
        } catch (e: SecurityException) {
            null
        }
    }

    private fun onGetLocationButtonClick() {
        CoroutineScope(Dispatchers.Main).launch {
            val currentLatLng = getCurrentLocation()
            val firebaseLocation = FirebaseLocationProvider().getCurrentFirebaseLocation()
            if (currentLatLng != null && firebaseLocation != null) {
                val firebaseLatLng =
                    LatLng(firebaseLocation.latitude, firebaseLocation.longitude)
                drawRoute(currentLatLng, firebaseLatLng)
            }
        }
    }

    private suspend fun drawRoute(start: LatLng, end: LatLng) = withContext(Dispatchers.Main) {
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
                    onRouteUpdated(latLngPath)
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

    fun getMap(): GoogleMap {
        return map
    }

    fun updateRoute(route: List<LatLng>?) {
        if (route != null) {
            polyline?.remove()
            polyline = map.addPolyline(
                PolylineOptions()
                    .addAll(route)
                    .color(Color.BLUE)
                    .width(10f)
            )
        }
    }
}