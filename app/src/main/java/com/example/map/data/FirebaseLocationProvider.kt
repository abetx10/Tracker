package com.example.map.data

import android.location.Location
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirebaseLocationProvider {
    suspend fun getCurrentFirebaseLocation(): Location? = withContext(Dispatchers.IO) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            try {
                val documentSnapshot = FirebaseFirestore.getInstance()
                    .collection("locations")
                    .document(user.uid)
                    .get()
                    .await()

                val data = documentSnapshot.data
                if (data != null) {
                    val latitude = data["latitude"] as? Double
                    val longitude = data["longitude"] as? Double
                    if (latitude != null && longitude != null) {
                        return@withContext Location("").apply {
                            this.latitude = latitude
                            this.longitude = longitude
                        }
                    }
                }
            } catch (e: Exception) {
                // Handle exception
            }
        }
        return@withContext null
    }
}