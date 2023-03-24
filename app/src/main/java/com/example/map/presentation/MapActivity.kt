package com.example.map.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.auth.presentation.*
import com.example.map.R

class MapActivity : AppCompatActivity(), AuthNavigationListener, AuthRegistrationListener,
    NavigationListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            val loginFragment = LoginFragment()
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, loginFragment)
                .commit()
        }
    }

    override fun onSignUpRequested() {
        val signUpFragment = SignUpFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, signUpFragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onRegistrationSuccess() {
        val loginFragment = LoginFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, loginFragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onNavigateToMainFragment() {
        val locationMapFragment = LocationMapFragment()

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, locationMapFragment)
//            .addToBackStack(null)
            .commit()
    }
}