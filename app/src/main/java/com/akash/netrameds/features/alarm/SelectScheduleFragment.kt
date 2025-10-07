package com.akash.netrameds.features.alarm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.akash.netrameds.R
import com.akash.netrameds.databinding.FragmentSelectScheduleBinding
import com.google.android.material.chip.Chip

class SelectScheduleFragment : Fragment() {

    private var _binding: FragmentSelectScheduleBinding? = null
    private val binding get() = _binding!!

    // Retrieve arguments from the previous fragment
    private val args: SelectScheduleFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        setupRadioGroupListener()
    }

    private fun setupRadioGroupListener() {
        binding.scheduleRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            // Show the day picker only when "Custom Days" is selected
            binding.dayPickerChipGroup.isVisible = (checkedId == R.id.radio_button_custom)
        }
    }

    private fun setupClickListeners() {
        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        binding.previousButton.setOnClickListener { findNavController().navigateUp() }

        // ... (inside the SelectScheduleFragment class, within the setupClickListeners function)
        binding.nextButton.setOnClickListener {
            val schedule: String
            val selectedDaysList = mutableListOf<String>()

            if (binding.radioButtonDaily.isChecked) {
                schedule = "Daily"
                // For "Daily", we can pass all days of the week
                selectedDaysList.addAll(listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"))
            } else {
                schedule = "Custom"
                binding.dayPickerChipGroup.children
                    .filter { (it as Chip).isChecked }
                    .forEach { selectedDaysList.add((it as Chip).text.toString()) }

                if (selectedDaysList.isEmpty()) {
                    Toast.makeText(requireContext(), "Please select at least one day", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            val medicineName = args.medicineName
            val medicineType = args.medicineType
            val dosage = args.dosage

            // Convert list to Array for navigation arguments
            val selectedDaysArray = selectedDaysList.toTypedArray()

            // Use the generated Directions class to navigate
            val action = SelectScheduleFragmentDirections.actionSelectScheduleFragmentToDosageTimesFragment(
                medicineName,
                medicineType,
                dosage,
                selectedDaysArray
            )
            findNavController().navigate(action)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
