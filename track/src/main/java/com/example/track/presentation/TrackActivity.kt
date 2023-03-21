package com.example.track.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.auth.presentation.AuthNavigationListener
import com.example.auth.presentation.NavigationListener
import com.example.auth.presentation.LoginFragment
import com.example.track.R

class TrackActivity : AppCompatActivity(), AuthNavigationListener, NavigationListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.track_activity, LoginFragment())
                .commit()
        }
    }

    override fun onSignUpRequested() {
        Toast.makeText(this, "You are already registered. Please sign in", Toast.LENGTH_LONG).show()
    }

    override fun onNavigateToMainFragment() {
        val trackFragment = TrackFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.track_activity, trackFragment)
            .addToBackStack(null)
            .commit()
    }
}