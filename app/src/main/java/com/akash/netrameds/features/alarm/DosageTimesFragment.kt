package com.akash.netrameds.features.alarm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.akash.netrameds.R
import com.akash.netrameds.databinding.FragmentDosageTimesBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DosageTimesFragment : Fragment() {

    private var _binding: FragmentDosageTimesBinding? = null
    private val binding get() = _binding!!

    private val args: DosageTimesFragmentArgs by navArgs()
    // Stores the selected time for each dose index
    private val selectedTimes = mutableMapOf<Int, Calendar>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDosageTimesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        updateTimePickers(1) // Initialize with one time picker
    }

    // ... inside the DosageTimesFragment class

    private fun setupClickListeners() {
        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        binding.previousButton.setOnClickListener { findNavController().navigateUp() }

        var timesPerDay = 1
        binding.buttonIncrement.setOnClickListener {
            if (timesPerDay < 10) { // Set a reasonable limit
                timesPerDay++
                binding.timesPerDayCount.text = timesPerDay.toString()
                updateTimePickers(timesPerDay)
            }
        }

        binding.buttonDecrement.setOnClickListener {
            if (timesPerDay > 1) {
                timesPerDay--
                binding.timesPerDayCount.text = timesPerDay.toString()
                updateTimePickers(timesPerDay)
            }
        }

        // --- THIS IS THE CORRECTED LISTENER ---
        binding.nextButton.setOnClickListener {
            // First, save the current values from all visible pickers.
            saveAllSelectedTimes()

            val timesToSet = binding.timesPerDayCount.text.toString().toInt()

            // Now, check if all times have been set.
            if (selectedTimes.size < timesToSet) {
                Toast.makeText(requireContext(), "Please set all dosage times", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // Stop execution if times are missing
            }

            // Collect and format the fresh times from the map.
            val formattedTimes = selectedTimes.values
                .sorted() // Sort by time of day for consistency
                .map { calendar ->
                    SimpleDateFormat("hh:mm a", Locale.getDefault()).format(calendar.time)
                }.toTypedArray()

            // Navigate to the summary screen with the complete and correct data.
            val action = DosageTimesFragmentDirections.actionDosageTimesFragmentToSummaryFragment(
                medicineName = args.medicineName,
                medicineType = args.medicineType,
                dosage = args.dosage,
                selectedDays = args.selectedDays,
                alarmTimes = formattedTimes
            )
            findNavController().navigate(action)
        }
    } // End of setupClickListeners()

// ... rest of the file


    private fun updateTimePickers(count: Int) {
        // First, save the current state before clearing
        saveAllSelectedTimes()

        binding.timePickerContainer.removeAllViews()

        // Re-add views up to the new count
        for (i in 0 until count) {
            val timePickerLayout = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_time_picker, binding.timePickerContainer, false)

            // Get views from the inflated layout
            val doseLabel = timePickerLayout.findViewById<TextView>(R.id.dose_number_label)
            val hourPicker = timePickerLayout.findViewById<NumberPicker>(R.id.hour_picker)
            val minutePicker = timePickerLayout.findViewById<NumberPicker>(R.id.minute_picker)
            val amPmPicker = timePickerLayout.findViewById<NumberPicker>(R.id.am_pm_picker)

            // Set a unique ID for the root view to use as a key
            timePickerLayout.id = View.generateViewId()

            doseLabel.text = "Time ${i + 1}"

            // Configure the number pickers
            setupNumberPickers(hourPicker, minutePicker, amPmPicker)

            // Restore the saved time for this index or set a default
            val calendar = selectedTimes[i] ?: Calendar.getInstance().apply {
                // Set a default time like 9:00 AM
                set(Calendar.HOUR_OF_DAY, 9)
                set(Calendar.MINUTE, 0)
            }
            restorePickerState(calendar, hourPicker, minutePicker, amPmPicker)

            // Add the complete layout to the container
            binding.timePickerContainer.addView(timePickerLayout)
        }
        // After adding all views, save the initial state
        saveAllSelectedTimes()
    }

    private fun setupNumberPickers(hourPicker: NumberPicker, minutePicker: NumberPicker, amPmPicker: NumberPicker) {
        hourPicker.minValue = 1
        hourPicker.maxValue = 12
        hourPicker.setFormatter { i -> String.format("%02d", i) } // e.g., "01", "09"

        minutePicker.minValue = 0
        minutePicker.maxValue = 59
        minutePicker.setFormatter { i -> String.format("%02d", i) } // e.g., "00", "05"

        amPmPicker.minValue = 0
        amPmPicker.maxValue = 1
        amPmPicker.displayedValues = arrayOf("AM", "PM")
    }

    private fun restorePickerState(calendar: Calendar, hourPicker: NumberPicker, minutePicker: NumberPicker, amPmPicker: NumberPicker) {
        var hour = calendar.get(Calendar.HOUR)
        if (hour == 0) hour = 12 // 12-hour format uses 12 for midnight/noon

        hourPicker.value = hour
        minutePicker.value = calendar.get(Calendar.MINUTE)
        amPmPicker.value = calendar.get(Calendar.AM_PM) // 0 for AM, 1 for PM
    }

    private fun saveAllSelectedTimes() {
        // Loop through each child view in the container
        for (i in 0 until binding.timePickerContainer.childCount) {
            val view = binding.timePickerContainer.getChildAt(i)
            val hourPicker = view.findViewById<NumberPicker>(R.id.hour_picker)
            val minutePicker = view.findViewById<NumberPicker>(R.id.minute_picker)
            val amPmPicker = view.findViewById<NumberPicker>(R.id.am_pm_picker)

            val cal = Calendar.getInstance().apply {
                // Number picker for 12-hour format needs special handling
                var hour = hourPicker.value
                if (hour == 12) hour = 0 // Calendar uses 0 for 12 AM/PM

                set(Calendar.HOUR, hour)
                set(Calendar.MINUTE, minutePicker.value)
                set(Calendar.AM_PM, amPmPicker.value)
                set(Calendar.SECOND, 0)
            }
            // Use the index as the key
            selectedTimes[i] = cal
        }
    }

    override fun onDestroyView() {
        // Save final state before view is destroyed
        saveAllSelectedTimes()
        super.onDestroyView()
        _binding = null
    }
}
