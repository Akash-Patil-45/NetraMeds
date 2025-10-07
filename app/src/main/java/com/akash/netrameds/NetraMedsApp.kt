package com.akash.netrameds

import android.app.Application
import com.google.firebase.FirebaseApp


class NetraMedsApp : Application() {

    override fun onCreate() {super.onCreate()

        // Initialize Firebase when the application starts
        FirebaseApp.initializeApp(this)
    }
}
