package com.akash.netrameds.features.scan

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope // <-- ADD THIS IMPORT
import androidx.navigation.fragment.findNavController
import com.akash.netrameds.R
import com.akash.netrameds.data.AppDatabase // <-- ADD THIS IMPORT
import com.akash.netrameds.data.ScanHistoryDao // <-- ADD THIS IMPORT
import com.akash.netrameds.model.ScanHistory // <-- ADD THIS IMPORT
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.util.Calendar // <-- ADD THIS IMPORT
import kotlinx.coroutines.launch // <-- ADD THIS IMPORT
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs

class ScanFragment : Fragment(R.layout.fragment_scan_medicine) {

    private val viewModel: ScanViewModel by viewModels()
    private var imageCapture: ImageCapture? = null

    private lateinit var cameraPreviewView: PreviewView
    private lateinit var brandNameTextView: TextView
    private lateinit var expiryDateTextView: TextView
    private lateinit var resultLayout: LinearLayout
    private lateinit var doneButton: Button

    private val handler = Handler(Looper.getMainLooper())
    private var scanRunnable: Runnable? = null
    private var isScanLoopRunning = false

    private lateinit var historyDao: ScanHistoryDao // <-- ADD THIS VARIABLE
    private val args: ScanFragmentArgs by navArgs()

    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission())
        { isGranted: Boolean ->
            if (isGranted) {
                startCamera()
            } else {
                Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cameraPreviewView = view.findViewById(R.id.camera_preview)
        brandNameTextView = view.findViewById(R.id.brandNameTextView)
        expiryDateTextView = view.findViewById(R.id.expiryDateTextView)
        resultLayout = view.findViewById(R.id.result_layout)
        doneButton = view.findViewById(R.id.done_button)

        // --- ADDED: Initialize the DAO ---
        historyDao = AppDatabase.getDatabase(requireContext()).scanHistoryDao()
        // ---

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            activityResultLauncher.launch(Manifest.permission.CAMERA)
        }

        doneButton.setOnClickListener {
            findNavController().popBackStack()
        }

        viewModel.medicineData.observe(viewLifecycleOwner) { medicineData ->
            if (medicineData != null) {
                val brandName = medicineData.brand_name ?: "Not found"
                val expiryDate = medicineData.expiry_date ?: "Not found"

                if (brandName != "Not found" && brandName.isNotEmpty()) {
                    // --- MEDICINE FOUND ---
                    stopAutoScanLoop()
                    brandNameTextView.text = brandName
                    expiryDateTextView.text = expiryDate

                    val announcement = "Medicine found. Brand Name: $brandName. Expiry Date: $expiryDate."
                    view.announceForAccessibility(announcement)
                    resultLayout.contentDescription = announcement

                    // --- ADDED: Save the successful scan to history ---
                    saveScanToHistory(brandName, expiryDate)

                    if (args.isForResult) {
                        // If called for a result (from Create Alarm), send result and go back
                        setFragmentResult("requestKey", bundleOf("medicineName" to brandName))

                        // We might want to show the user it was found before popping
                        // For now, let's add a small delay or use the Done button logic
                        // But for auto-redirect:
                        findNavController().popBackStack()
                    } else {
                        // Standard mode: Save to history
                        saveScanToHistory(brandName, medicineData.expiry_date ?: "")
                    }
                    // ---

                } else {
                    // Not found: continue loop
                    brandNameTextView.text = "Scanning..."
                    expiryDateTextView.text = "..."
                    view.announceForAccessibility("Scanning...")
                    resultLayout.contentDescription = "Scanning for medicine details."
                    scheduleNextScan()
                }
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage != null) {
                Log.e("ScanFragment", "Scan Error: $errorMessage")
                brandNameTextView.text = "Error"
                expiryDateTextView.text = "Retrying..."
                view.announceForAccessibility("Error scanning. Retrying.")
                resultLayout.contentDescription = "Error scanning. Retrying."
                scheduleNextScan()
            }
        }
    }

    // --- ADDED: This function saves the scan result to the Room database ---
    private fun saveScanToHistory(medicineName: String, expiryDate: String) {
        lifecycleScope.launch {
            val newHistoryItem = ScanHistory(
                medicineName = medicineName,
                expiryDate = expiryDate, // e.g., "10/2026"
                scanTimestamp = Calendar.getInstance().timeInMillis // The current time
            )
            historyDao.insert(newHistoryItem)
            Log.d("ScanFragment", "Successfully saved to scan history: $medicineName")
        }
    }
    // ---

    private fun allPermissionsGranted() = ContextCompat.checkSelfPermission(
        requireContext(), Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(cameraPreviewView.surfaceProvider)
            }
            imageCapture = ImageCapture.Builder().build()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
                startAutoScanLoop()
            } catch(exc: Exception) {
                Log.e("ScanFragment", "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    // ... (rest of the file: start/stop scan loop, takePhoto, onDestroy, helpers) ...
    private fun startAutoScanLoop() {
        Log.d("ScanFragment", "Starting auto-scan loop")
        view?.announceForAccessibility("Scanning for medicine details.")
        resultLayout.contentDescription = "Scanning for medicine details."
        isScanLoopRunning = true
        scanRunnable = Runnable {
            if (!isScanLoopRunning) return@Runnable
            Log.d("ScanFragment", "Taking auto-scan photo")
            takePhoto()
        }
        handler.post(scanRunnable!!)
    }

    private fun stopAutoScanLoop() {
        Log.d("ScanFragment", "Stopping auto-scan loop")
        isScanLoopRunning = false
        scanRunnable?.let { handler.removeCallbacks(it) }
    }

    private fun scheduleNextScan() {
        if (!isScanLoopRunning) return
        scanRunnable?.let { handler.postDelayed(it, 1500) }
    }

    private fun takePhoto() {
        val imageCapture = this.imageCapture ?: return
        imageCapture.takePicture(
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    val originalBitmap = imageProxyToBitmap(image)
                    val resizedBitmap = resizeBitmap(originalBitmap, 800)
                    val base64String = bitmapToBase64(resizedBitmap)
                    val fullBase64String = "data:image/jpeg;base64,$base64String"
                    viewModel.processImage(fullBase64String)
                    image.close()
                }
                override fun onError(exception: ImageCaptureException) {
                    Log.e("ScanFragment", "Photo capture failed: ${exception.message}", exception)
                    scheduleNextScan()
                }
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopAutoScanLoop()
    }

    private fun imageProxyToBitmap(image: ImageProxy): Bitmap {
        val buffer: ByteBuffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        val rotationDegrees = image.imageInfo.rotationDegrees
        if (rotationDegrees == 0) {
            return bitmap
        }
        val matrix = Matrix()
        matrix.postRotate(rotationDegrees.toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun resizeBitmap(bitmap: Bitmap, maxSize: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        if (width <= maxSize && height <= maxSize) {
            return bitmap
        }
        val ratio: Float = if (width > height) {
            maxSize.toFloat() / width
        } else {
            maxSize.toFloat() / height
        }
        val newWidth = (width * ratio).toInt()
        val newHeight = (height * ratio).toInt()
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }
}