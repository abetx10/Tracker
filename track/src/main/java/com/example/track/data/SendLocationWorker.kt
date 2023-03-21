package com.example.track.data

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters


class SendLocationWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val locationRepository = LocationRepository(applicationContext)
        val locationUpdates = locationRepository.getAllLocationUpdates()
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null && NetworkUtils.isInternetAvailable(applicationContext)) {
            locationUpdates.forEach { locationUpdate ->
                val data = hashMapOf(
                    "latitude" to locationUpdate.latitude,
                    "longitude" to locationUpdate.longitude,
                    "timestamp" to locationUpdate.timestamp
                )

                FirebaseFirestore.getInstance()
                    .collection("locations")
                    .document(user.uid)
                    .set(data)

                locationRepository.deleteAllLocationUpdates()
            }
            return Result.success()
        } else {
            return Result.retry()
        }
    }
}