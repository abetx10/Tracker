package com.example.track.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [LocationUpdate::class], version = 1)
abstract class LocationDatabase : RoomDatabase() {
    abstract fun locationUpdateDao(): LocationUpdateDao

    companion object {
        private var instance: LocationDatabase? = null

        fun getInstance(context: Context): LocationDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    LocationDatabase::class.java,
                    "location_database"
                ).build()
            }
            return instance!!
        }
    }
}
