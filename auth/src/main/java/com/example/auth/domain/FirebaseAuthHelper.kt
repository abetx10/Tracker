package com.example.auth.domain

import com.example.auth.data.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class FirebaseAuthHelper {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun signInWithEmailPassword(userProfile: UserProfile, onResult: (FirebaseUser?, Exception?) -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(userProfile.email, userProfile.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(firebaseAuth.currentUser, null)
                } else {
                    onResult(null, task.exception)
                }
            }
    }
}