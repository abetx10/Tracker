package com.example.auth.domain

import android.app.Activity
import android.content.Context
import android.widget.Toast
import com.example.auth.data.UserProfile
import com.example.auth.presentation.AuthRegistrationListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpUser(private val context: Context, private val authRegistrationListener: AuthRegistrationListener) {
    private val activity = context as Activity

    fun registerUser(userProfile: UserProfile) {
        val auth: FirebaseAuth = Firebase.auth

        auth.createUserWithEmailAndPassword(userProfile.email, userProfile.password)
            .addOnCompleteListener { task ->
                activity.runOnUiThread {
                    if (task.isSuccessful) {
                        Toast.makeText(context, "SignUp successful", Toast.LENGTH_LONG).show()
                        authRegistrationListener.onRegistrationSuccess()
                    } else {
                        Toast.makeText(
                            context,
                            "SignUp invalid: ${task.exception?.localizedMessage ?: "Unknown error"}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
    }
}