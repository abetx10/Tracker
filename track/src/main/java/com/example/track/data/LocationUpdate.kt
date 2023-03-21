package com.example.track.data

import androidx.room.*

@Entity(tableName = "location_updates")
data class LocationUpdate(
    @PrimaryKey val id: String,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long
)

@Dao
interface LocationUpdateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocationUpdate(locationUpdate: LocationUpdate)

    @Query("SELECT * FROM location_updates")
    suspend fun getAllLocationUpdates(): List<LocationUpdate>

    @Query("DELETE FROM location_updates")
    suspend fun deleteAllLocationUpdates()
}