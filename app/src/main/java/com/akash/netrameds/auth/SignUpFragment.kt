package com.akash.netrameds.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.akash.netrameds.R
import com.akash.netrameds.databinding.FragmentSignUpBinding
import com.google.firebase.auth.FirebaseAuth // Import Firebase Auth
import com.google.firebase.auth.ktx.auth // Import KTX extension
import com.google.firebase.ktx.Firebase // Import Firebase

class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding
    private lateinit var auth: FirebaseAuth // Declare the Auth variable

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up, container, false)

        // Initialize Firebase Auth
        auth = Firebase.auth

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Navigation to Login
        binding.tvAlreadyHaveAccount.setOnClickListener {
            findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
        }

        // --- SIGN-UP LOGIC ---
        binding.signupButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            // Basic input validation
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "Email and password cannot be empty.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create the user in Firebase
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        // Sign up success
                        Log.d("SignUpFragment", "createUserWithEmail:success")
                        Toast.makeText(context, "Account created successfully! Please login.", Toast.LENGTH_LONG).show()

                        // Navigate to the login screen
                        findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)

                    } else {
                        // If sign up fails, display a message to the user.
                        Log.w("SignUpFragment", "createUserWithEmail:failure", task.exception)
                        Toast.makeText(context, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}
