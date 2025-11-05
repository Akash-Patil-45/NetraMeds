plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    id("com.google.gms.google-services")
    id("androidx.navigation.safeargs.kotlin")

}
android {
    namespace = "com.akash.netrameds"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.akash.netrameds"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        // Correctly enabled dataBinding and viewBinding
        dataBinding = true
        viewBinding = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    // Core & UI
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material) // Material is already here
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation("androidx.concurrent:concurrent-futures-ktx:1.1.0")

    // Jetpack Navigation
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
// Gson Converter - Converts JSON to Kotlin data classes
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
// OkHttp Logging Interceptor (Very useful for debugging network calls)
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
// Coroutine support (for making calls in the background)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    // ViewModel & LiveData (MVVM)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.livedata.ktx)

    // Room Database (Corrected and de-duplicated)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    kapt(libs.room.compiler) // This is the correct way for your setup

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.google.auth)

    // CameraX
    implementation(libs.camera.core)
    implementation(libs.camera.camera2)
    implementation(libs.camera.lifecycle)
    implementation(libs.camera.view)

    // ML Kit
    implementation(libs.mlkit.text.recognition)

    // Coil Image Loading
    implementation(libs.coil)

    implementation("com.google.guava:guava:33.0.0-android")

    // ... rest of your dependencies
    implementation("androidx.concurrent:concurrent-futures-ktx:1.1.0")

    // Jetpack Navigation
    implementation(libs.navigation.fragment.ktx)


    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation("com.google.android.material:material:1.11.0")

    // ALL THE REDUNDANT DEPENDENCIES BELOW HAVE BEEN REMOVED.
}