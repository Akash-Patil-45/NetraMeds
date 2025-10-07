package com.akash.netrameds.features.alarm

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.akash.netrameds.databinding.FragmentAddDosageBinding
import java.util.Locale

class AddDosageFragment : Fragment() {

    private var _binding: FragmentAddDosageBinding? = null
    private val binding get() = _binding!!

    // Retrieve arguments from the previous fragment
    private val args: AddDosageFragmentArgs by navArgs()

    // --- Launcher for Speech Recognition ---
    private val speechRecognizerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!results.isNullOrEmpty()) {
                binding.dosageEditText.setText(results[0])
            }
        }
    }

    // --- Launcher for Permission Request ---
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            launchSpeechToText()
        } else {
            Toast.makeText(requireContext(), "Microphone permission is required.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddDosageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        binding.previousButton.setOnClickListener { findNavController().navigateUp() }

        binding.voiceInputButton.setOnClickListener { checkPermissionAndLaunch() }

        binding.nextButton.setOnClickListener {
            val dosage = binding.dosageEditText.text.toString()
            if (dosage.isBlank()) {
                binding.dosageLayout.error = "Dosage cannot be empty"
            } else {
                binding.dosageLayout.error = null
                val medicineName = args.medicineName
                val medicineType = args.medicineType

                val action = AddDosageFragmentDirections.actionAddDosageFragmentToSelectScheduleFragment(
                    medicineName,
                    medicineType,
                    dosage
                )
                findNavController().navigate(action)
                // TODO: Navigate to the next screen (4/6)
                // val action = AddDosageFragmentDirections.actionToAddStrengthFragment(medicineName, medicineType, dosage)
                // findNavController().navigate(action)
            }
        }
    }

    private fun checkPermissionAndLaunch() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED -> {
                launchSpeechToText()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    private fun launchSpeechToText() {
        val speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Say the dosage")
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
