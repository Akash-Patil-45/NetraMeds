package com.akash.netrameds.network

// In ApiService.kt
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    // This matches your Python @app.route('/process_image', methods=['POST'])
    @POST("process_image")
    suspend fun processImage(
        @Body request: ImageRequest
    ): Response<MedicineResponse>
}