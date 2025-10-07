package com.akash.netrameds.features.alarm

import android.Manifest // <-- IMPORT THIS
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager // <-- IMPORT THIS
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat // <-- IMPORT THIS
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.akash.netrameds.databinding.FragmentAddMedicineNameBinding
import java.util.Locale

class AddMedicineNameFragment : Fragment() {

    private var _binding: FragmentAddMedicineNameBinding? = null
    private val binding get() = _binding!!

    // --- Launcher for Speech Recognition ---
    private val speechRecognizerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!results.isNullOrEmpty()) {
                binding.medicineNameEditText.setText(results[0])
            }
        }
    }

    // --- ADD: Launcher for Permission Request ---
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission is granted. Launch the speech recognizer.
            launchSpeechToText()
        } else {
            // Permission denied. Show a message to the user.
            Toast.makeText(
                requireContext(),
                "Microphone permission is required for voice input.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddMedicineNameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        // --- UPDATE: Voice input click now checks for permission ---
        binding.voiceInputButton.setOnClickListener {
            checkPermissionAndLaunch()
        }

        // ... (rest of the onViewCreated method remains the same) ...
        binding.nextButton.setOnClickListener {
            val medicineName = binding.medicineNameEditText.text.toString()
            if (medicineName.isBlank()) {
                binding.medicineNameLayout.error = "Medicine name cannot be empty"
            } else {
                binding.medicineNameLayout.error = null // Clear error

                // Use the generated Directions class to navigate and pass the name
                val action = AddMedicineNameFragmentDirections.actionAddMedicineNameFragmentToSelectMedicineTypeFragment(medicineName)
                findNavController().navigate(action)
            }
        }
    }

    // --- ADD: Function to check permission ---
    private fun checkPermissionAndLaunch() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Permission is already granted, launch the speech recognizer.
                launchSpeechToText()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO) -> {
                // Explain to the user why the permission is needed (optional)
                Toast.makeText(requireContext(), "Microphone access is needed for voice input.", Toast.LENGTH_LONG).show()
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
            else -> {
                // Directly request the permission.
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    // --- RENAME: This function is now just for launching ---
    private fun launchSpeechToText() {
        val speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Say the medicine name")
        }

        try {
            speechRecognizerLauncher.launch(speechIntent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Your device doesn't support speech input", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
