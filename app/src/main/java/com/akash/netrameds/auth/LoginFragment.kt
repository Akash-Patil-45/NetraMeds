package com.akash.netrameds.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.akash.netrameds.R
import com.akash.netrameds.databinding.FragmentLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient // For Google Sign-In

    // ActivityResultLauncher for the Google Sign-In flow
    private val googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            // Google Sign In was successful, authenticate with Firebase
            val account = task.getResult(ApiException::class.java)!!
            Log.d("GoogleSignIn", "firebaseAuthWithGoogle:" + account.id)
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            // Google Sign In failed
            Log.w("GoogleSignIn", "Google sign in failed", e)
            Toast.makeText(requireContext(), "Google Sign-In failed.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        auth = Firebase.auth

        // --- GOOGLE SIGN-IN SETUP ---
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // This ID is essential for Firebase
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        // --- END GOOGLE SIGN-IN SETUP ---

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Navigation to Sign Up
        binding.signUpText.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }

        // --- FORGOT PASSWORD LOGIC ---
        binding.forgotPasswordText.setOnClickListener {
            showForgotPasswordDialog()
        }

        // --- GOOGLE LOGIN LOGIC ---
        binding.googleLoginButton.setOnClickListener {
            signInWithGoogle()
        }

        // --- LOGIN LOGIC ---
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Email and password cannot be empty.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        Log.d("LoginFragment", "signInWithEmail:success")
                        Toast.makeText(requireContext(), "Login Successful!", Toast.LENGTH_SHORT).show()

                        // --- NAVIGATION ADDED HERE (Step 1) ---
                        navigateToHome()

                    } else {
                        Log.w("LoginFragment", "signInWithEmail:failure", task.exception)
                        Toast.makeText(requireContext(), "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    // --- GOOGLE SIGN-IN HELPER FUNCTIONS ---
    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success
                    Log.d("LoginFragment", "signInWithCredential:success")
                    Toast.makeText(requireContext(), "Google Sign-In Successful!", Toast.LENGTH_SHORT).show()

                    // --- NAVIGATION ADDED HERE (Step 2) ---
                    navigateToHome()

                } else {
                    // If sign in fails
                    Log.w("LoginFragment", "signInWithCredential:failure", task.exception)
                    Toast.makeText(requireContext(), "Authentication Failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // --- HELPER FUNCTION FOR NAVIGATION ---
    private fun navigateToHome() {
        // This action ID must exist in your nav_graph.xml, connecting LoginFragment to HomeFragment
        val action = R.id.action_loginFragment_to_homeFragment
        findNavController().navigate(action)
    }


    // --- HELPER FUNCTION FOR FORGOT PASSWORD ---
    private fun showForgotPasswordDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Forgot Password")
        builder.setMessage("Enter your registered email to receive a password reset link.")

        // Create an EditText for the dialog
        val input = EditText(requireContext())
        input.hint = "Email Address"
        builder.setView(input)

        // Set up the buttons
        builder.setPositiveButton("Send") { dialog, _ ->
            val email = input.text.toString().trim()
            if (email.isNotEmpty()) {
                sendPasswordResetEmail(email)
            } else {
                Toast.makeText(requireContext(), "Please enter your email.", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("ForgotPassword", "Email sent.")
                    Toast.makeText(requireContext(), "Password reset link sent to your email.", Toast.LENGTH_LONG).show()
                } else {
                    Log.w("ForgotPassword", "sendEmail:failure", task.exception)
                    Toast.makeText(requireContext(), "Failed to send reset link: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }
}
