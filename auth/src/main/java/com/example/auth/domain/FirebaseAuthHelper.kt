package com.example.auth.domain

import com.example.auth.data.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FirebaseAuthHelper {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun signInWithEmailPassword(
        userProfile: UserProfile,
        onResult: (FirebaseUser?, Exception?) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result =
                    firebaseAuth.signInWithEmailAndPassword(userProfile.email, userProfile.password)
                        .await()
                onResult(result.user, null)
            } catch (e: Exception) {
                onResult(null, e)
            }
        }
    }
}