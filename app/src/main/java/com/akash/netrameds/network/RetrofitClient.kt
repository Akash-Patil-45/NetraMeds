// In RetrofitClient.kt
package com.akash.netrameds.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit // <-- 1. ADD THIS IMPORT

object RetrofitClient {

    private const val BASE_URL = "https://netrameds-server.onrender.com/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Create an OkHttp client with the logger
    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(60, TimeUnit.SECONDS) // <-- 2. ADD THIS
        .readTimeout(60, TimeUnit.SECONDS)    // <-- 3. ADD THIS
        .writeTimeout(60, TimeUnit.SECONDS)   // <-- 4. ADD THIS
        .build()

    // Create the lazy-initialized Retrofit instance
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient) // Use the custom OkHttp client
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Publicly expose the ApiService
    val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}