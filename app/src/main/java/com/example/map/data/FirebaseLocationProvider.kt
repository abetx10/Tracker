package com.example.map.data

import android.location.Location
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseLocationProvider {
    fun getCurrentFirebaseLocation(callback: (location: Location?) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            FirebaseFirestore.getInstance()
                .collection("locations")
                .document(user.uid)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val data = documentSnapshot.data
                    if (data != null) {
                        val latitude = data["latitude"] as? Double
                        val longitude = data["longitude"] as? Double
                        if (latitude != null && longitude != null) {
                            val location = Location("").apply {
                                this.latitude = latitude
                                this.longitude = longitude
                            }
                            callback(location)
                        } else {
                            callback(null)
                        }
                    } else {
                        callback(null)
                    }
                }
                .addOnFailureListener {
                    callback(null)
                }
        } else {
            callback(null)
        }
    }
}