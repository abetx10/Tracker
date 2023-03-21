package com.example.auth.presentation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.auth.data.UserProfile
import com.example.auth.databinding.FragmentSignUpBinding
import com.example.auth.domain.SignUpUser

class SignUpFragment : Fragment(), AuthRegistrationListener {

    private lateinit var binding: FragmentSignUpBinding
    private var authNavigationListener: AuthNavigationListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.btSignUp.setOnClickListener {
            val email = binding.edEmailSignUp.text.toString()
            val password = binding.edPasswordSignUp.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                SignUpUser(requireContext(), this).registerUser(UserProfile(email, password))
            } else {
                Toast.makeText(requireContext(), "Please enter email and password", Toast.LENGTH_LONG).show()
            }
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is AuthNavigationListener) {
            authNavigationListener = context
        } else {
            throw RuntimeException("$context must implement AuthNavigationListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        authNavigationListener = null
    }

    override fun onRegistrationSuccess() {
        authNavigationListener?.onSignUpRequested()
    }
}
