// In SplashActivity.kt

package com.akash.netrameds // Adjust package name if needed

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.akash.netrameds.auth.AuthActivity // Import AuthActivity
import com.google.firebase.auth.FirebaseAuth // Import FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SplashActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    // Add this companion object to define the TAG
    companion object {
        private const val TAG = "SplashActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Initialize Firebase Auth
        auth = Firebase.auth

        // Use a Handler to delay the check slightly (optional, but common for splash screens)
        Handler(Looper.getMainLooper()).postDelayed({
            checkUserStatus()
        }, 1500) // 1.5 second delay
    }

    private fun checkUserStatus() {
        Log.d(TAG, "Checking user status...") // Log start
        val currentUser = auth.currentUser

        if (currentUser != null) {
            // Log that user was found
            Log.d(TAG, "User FOUND: ${currentUser.uid}. Navigating to MainActivity.")
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            // Log that user was NOT found
            Log.d(TAG, "User NOT FOUND. Navigating to AuthActivity.")
            startActivity(Intent(this, AuthActivity::class.java))
        }
        finish()
    }
}
