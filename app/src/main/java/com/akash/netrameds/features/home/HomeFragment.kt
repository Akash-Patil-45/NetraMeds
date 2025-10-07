package com.akash.netrameds.features.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.akash.netrameds.R
import com.akash.netrameds.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    // Use nullable _binding to handle the view lifecycle
    private var _binding: FragmentHomeBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment using view binding
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- Set up Click Listeners for the Cards ---

        // This is the primary action: navigating to the scan page
        binding.medicineRecognitionCard.setOnClickListener {
            // Use the NavController to go from Home to Scan.
            // Ensure this action exists in your res/navigation/nav_graph.xml
            findNavController().navigate(R.id.action_homeFragment_to_scanMedicineFragment)
        }

        // --- Other Click Listeners for UI elements ---

        binding.settingsButton.setOnClickListener {
            // Placeholder for settings navigation or action
            Toast.makeText(requireContext(), "Settings clicked!", Toast.LENGTH_SHORT).show()
        }

        binding.medicineHistoryCard.setOnClickListener {
            // Placeholder for medicine history navigation
            Toast.makeText(requireContext(), "Medicine History clicked!", Toast.LENGTH_SHORT).show()
        }

        binding.smartAlarmCard.setOnClickListener {
            // Use the new action to navigate to the CreateAlarmFragment
            findNavController().navigate(R.id.action_homeFragment_to_createAlarmFragment)
        }

        binding.supportCard.setOnClickListener {
            // Placeholder for support feature
            Toast.makeText(requireContext(), "Support clicked!", Toast.LENGTH_SHORT).show()
        }


        // --- Handle Bottom Navigation Menu Clicks ---

    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clean up the binding object to avoid memory leaks
        _binding = null
    }
}
