package com.akash.netrameds.features.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.akash.netrameds.R
import com.akash.netrameds.auth.AuthActivity
import com.akash.netrameds.databinding.FragmentHomeBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

/**
 * HomeFragment displays the main dashboard screen after user login.
 * It contains navigation options to various app features like
 * Medicine Recognition, Smart Alarm, Settings, etc.
 */
class HomeFragment : Fragment() {

    // View binding variable (nullable to handle lifecycle safely)
    private var _binding: FragmentHomeBinding? = null

    // Non-nullable getter to access binding safely
    private val binding get() = _binding!!

    /**
     * Called to inflate the layout for this fragment.
     * The binding object connects the XML layout with Kotlin code.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout using view binding
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Called immediately after onCreateView().
     * All view-related logic (like click listeners) should go here.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- Click listener for Settings button ---
        // Navigates user to the Settings screen (SettingsFragment)
        binding.settingsButton.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
        }

        // --- Click listener for Medicine Recognition card ---
        // Navigates to Scan Medicine feature
        binding.medicineRecognitionCard.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_scanMedicineFragment)
        }

        // --- Click listener for Medicine History card ---
        // Currently shows a simple toast message (can be updated later)
        binding.medicineHistoryCard.setOnClickListener {
            Toast.makeText(requireContext(), "Medicine History clicked!", Toast.LENGTH_SHORT).show()
        }

        // --- Click listener for Smart Alarm card ---
        // Navigates to Create Alarm screen
        binding.smartAlarmCard.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_createAlarmFragment)
        }

        // --- Click listener for Support card ---
        // Currently shows a toast; can later open Support screen or contact form
        binding.supportCard.setOnClickListener {
            Toast.makeText(requireContext(), "Support clicked!", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Called when the view hierarchy is being destroyed.
     * Sets binding to null to avoid memory leaks.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}