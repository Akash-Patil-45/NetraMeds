package com.akash.netrameds.network

// In ApiData.kt

// This matches the JSON you send *to* the server
data class ImageRequest(
    val image: String // The Base64 image string
)

// This matches the JSON your server sends *back*
data class MedicineResponse(
    val brand_name: String?,
    val expiry_date: String?
)