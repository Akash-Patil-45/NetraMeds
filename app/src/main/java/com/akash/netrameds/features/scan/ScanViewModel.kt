package com.akash.netrameds.features.scan

// In com/akash/netrameds/features/scan/ScanViewModel.kt
// (or a new com/akash/netrameds/viewmodel/ScanViewModel.kt)



import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akash.netrameds.network.ImageRequest
import com.akash.netrameds.network.MedicineResponse
import com.akash.netrameds.network.RetrofitClient
import kotlinx.coroutines.launch

class ScanViewModel : ViewModel() {

    // 1. Private LiveData that only the ViewModel can change
    private val _medicineData = MutableLiveData<MedicineResponse?>()

    // 2. Public LiveData that the Fragment can observe
    val medicineData: LiveData<MedicineResponse?> = _medicineData

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    /**
     * This is where your code snippet's logic goes.
     * It's now running in viewModelScope, which is lifecycle-aware
     * and much safer than GlobalScope.
     */
    fun processImage(base64Image: String) {

        // Create the request object
        val request = ImageRequest(image = base64Image)

        // Launch a coroutine in the ViewModel's own scope
        viewModelScope.launch {
            try {
                // Use the RetrofitClient object to make the call
                val response = RetrofitClient.api.processImage(request)

                if (response.isSuccessful) {
                    // Post the result to the LiveData
                    _medicineData.postValue(response.body())
                } else {
                    Log.e("ScanViewModel", "Server Error: ${response.code()}")
                    _error.postValue("Server Error: ${response.code()}")
                }

            } catch (e: Exception) {
                Log.e("ScanViewModel", "Network Failure: ${e.message}")
                _error.postValue("Network Failure: ${e.message}")
            }
        }
    }
}