package com.example.auth.domain

import android.app.Activity
import android.content.Context
import android.widget.Toast
import com.example.auth.data.UserProfile
import com.example.auth.presentation.AuthRegistrationListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SignUpUser(private val context: Context, private val authRegistrationListener: AuthRegistrationListener) {
    private val activity = context as Activity

    fun registerUser(userProfile: UserProfile) {
        val auth: FirebaseAuth = Firebase.auth

        CoroutineScope(Dispatchers.IO).launch {
            try {
                auth.createUserWithEmailAndPassword(userProfile.email, userProfile.password).await()
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "SignUp successful", Toast.LENGTH_LONG).show()
                    authRegistrationListener.onRegistrationSuccess()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "SignUp invalid: ${e.localizedMessage ?: "Unknown error"}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}