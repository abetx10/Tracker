package com.example.track.data

import android.content.Context


class LocationRepository(context: Context) {
    private val locationUpdateDao: LocationUpdateDao

    init {
        val database = LocationDatabase.getInstance(context)
        locationUpdateDao = database.locationUpdateDao()
    }

    suspend fun insertLocationUpdate(locationUpdate: LocationUpdate) {
        locationUpdateDao.insertLocationUpdate(locationUpdate)
    }

    suspend fun getAllLocationUpdates(): List<LocationUpdate> {
        return locationUpdateDao.getAllLocationUpdates()
    }

    suspend fun deleteAllLocationUpdates() {
        locationUpdateDao.deleteAllLocationUpdates()
    }
}