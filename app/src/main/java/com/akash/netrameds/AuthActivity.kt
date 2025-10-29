package com.akash.netrameds.auth // This line must match the folder path

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.akash.netrameds.R

class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // This links your Kotlin file to the layout you provided
        setContentView(R.layout.activity_auth)
    }
}