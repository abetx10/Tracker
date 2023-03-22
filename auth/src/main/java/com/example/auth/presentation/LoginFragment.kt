package com.example.auth.presentation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.auth.R
import com.example.auth.data.UserProfile
import com.example.auth.databinding.FragmentLoginBinding
import com.example.auth.domain.FirebaseAuthHelper

class LoginFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentLoginBinding
    private var authNavigationListener: AuthNavigationListener? = null
    private var navigationListener: NavigationListener? = null
    private val firebaseAuthHelper = FirebaseAuthHelper()


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is AuthNavigationListener) {
            authNavigationListener = context
        } else {
            throw RuntimeException("$context must implement AuthNavigationListener")
        }
        if (context is NavigationListener) {
            navigationListener = context
        } else {
            throw RuntimeException("$context must implement LocationMapNavigationListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.btSignIn.setOnClickListener(this)
        binding.btDontHaveAcc.setOnClickListener(this)

        return view
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btSignIn -> {
                val email = binding.edEmailLogin.text.toString()
                val password = binding.edPasswordLogin.text.toString()
                val userProfile = UserProfile(email, password)

                firebaseAuthHelper.signInWithEmailPassword(userProfile) { firebaseUser, exception ->
                    requireActivity().runOnUiThread {
                        if (firebaseUser != null) {
                            navigationListener?.onNavigateToMainFragment()
                        } else {
                            val errorMessage =
                                exception?.localizedMessage ?: "Authentication failed."
                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
            R.id.btDontHaveAcc -> {
                authNavigationListener?.onSignUpRequested()
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        authNavigationListener = null
        navigationListener = null
    }
}