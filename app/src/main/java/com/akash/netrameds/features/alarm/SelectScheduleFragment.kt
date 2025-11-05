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
import com.google.android.material.datepicker.MaterialDatePicker // Import this
import java.text.SimpleDateFormat // Import this
import java.util.Date // Import this
import java.util.Locale // Import this
import java.util.TimeZone // Import this
import com.google.android.material.datepicker.CalendarConstraints // Add this
import com.google.android.material.datepicker.DateValidatorPointForward // Add this

class SelectScheduleFragment : Fragment() {

    private var _binding: FragmentSelectScheduleBinding? = null
    private val binding get() = _binding!!
    private val args: SelectScheduleFragmentArgs by navArgs()

    private var selectedDate: Long? = null

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
        setupRadioGroupListener() // This function now has the fix
        setupDatePicker()
    }

    // --- THIS IS THE CORRECTED LOGIC ---
    // It will hide and show the correct fields.
    private fun setupRadioGroupListener() {
        binding.scheduleRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            binding.dayPickerChipGroup.isVisible = (checkedId == R.id.radio_button_custom)
            binding.datePickerLayout.isVisible = (checkedId == R.id.radio_button_date)
        }
    }

    private fun setupDatePicker() {
        // Formatter for displaying the date in the text field
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC") // Use UTC for consistency

        binding.datePickerEditText.setOnClickListener {

            // --- NEW: Create a validator to disable past dates ---
            val constraintsBuilder =
                CalendarConstraints.Builder()
                    .setValidator(DateValidatorPointForward.now())

            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(selectedDate ?: MaterialDatePicker.todayInUtcMilliseconds())
                .setCalendarConstraints(constraintsBuilder.build()) // <-- ADD THIS LINE
                .build()

            datePicker.addOnPositiveButtonClickListener {
                // Store the selected date
                selectedDate = it
                // Format and display the date
                val dateString = sdf.format(Date(it))
                binding.datePickerEditText.setText(dateString)
            }

            datePicker.show(childFragmentManager, "DATE_PICKER")
        }
    }

    private fun setupClickListeners() {
        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        binding.previousButton.setOnClickListener { findNavController().navigateUp() }

        binding.nextButton.setOnClickListener {
            val selectedDaysList = mutableListOf<String>()

            when (binding.scheduleRadioGroup.checkedRadioButtonId) {
                R.id.radio_button_daily -> {
                    selectedDaysList.addAll(listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"))
                }
                R.id.radio_button_custom -> {
                    binding.dayPickerChipGroup.children
                        .filter { (it as Chip).isChecked }
                        .forEach { selectedDaysList.add((it as Chip).text.toString()) }
                    if (selectedDaysList.isEmpty()) {
                        Toast.makeText(requireContext(), "Please select at least one day", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                }
                R.id.radio_button_date -> {
                    val dateText = binding.datePickerEditText.text.toString()
                    if (dateText.isBlank()) {
                        Toast.makeText(requireContext(), "Please select a date", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    selectedDaysList.add(dateText)
                }
            }

            val medicineName = args.medicineName
            val medicineType = args.medicineType
            val dosage = args.dosage
            val selectedDaysArray = selectedDaysList.toTypedArray()

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