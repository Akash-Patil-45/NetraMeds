package com.akash.netrameds.features.alarm

import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.text.inSpans
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.akash.netrameds.R // Import for R.id.homeFragment
import com.akash.netrameds.model.Alarm // Make sure this import is correct
import com.akash.netrameds.databinding.FragmentSummaryBinding


class SummaryFragment : Fragment() {

    private var _binding: FragmentSummaryBinding? = null
    private val binding get() = _binding!!

    // Get the data passed from previous fragments
    private val args: SummaryFragmentArgs by navArgs()

    // Declare the scheduler
    private lateinit var alarmScheduler: AlarmScheduler

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSummaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the scheduler with the application context
        alarmScheduler = AlarmScheduler(requireContext().applicationContext)

        // Populate the UI with the summary data
        displaySummary()

        // Set up the click listeners for the buttons
        setupClickListeners()
    }

    private fun displaySummary() {
        val summaryBuilder = SpannableStringBuilder()

        // Build the formatted string with all alarm details
        summaryBuilder.inSpans(StyleSpan(Typeface.BOLD)) { append("Medicine: ") }
        summaryBuilder.append("${args.medicineName} (${args.medicineType})\n\n")

        summaryBuilder.inSpans(StyleSpan(Typeface.BOLD)) { append("Dosage: ") }
        summaryBuilder.append("${args.dosage}\n\n")

        val scheduleDays = args.selectedDays.joinToString(", ")
        val scheduleText = if (scheduleDays.equals("sun,mon,tue,wed,thu,fri,sat", ignoreCase = true)) "Daily" else scheduleDays
        summaryBuilder.inSpans(StyleSpan(Typeface.BOLD)) { append("Schedule: ") }
        summaryBuilder.append("$scheduleText\n\n")

        val timesCount = args.alarmTimes.size
        val timesString = args.alarmTimes.joinToString("\n")
        summaryBuilder.inSpans(StyleSpan(Typeface.BOLD)) { append("Times ($timesCount per day):\n") }
        summaryBuilder.append(timesString)

        // Set the text on the single TextView
        binding.fullSummaryText.text = summaryBuilder
    }

    /**
     * Sets up click listeners for the toolbar, 'Previous' button, and 'Create Alarm' button.
     */
    private fun setupClickListeners() {
        // Toolbar back navigation
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        // "Previous" button navigation
        binding.previousButton.setOnClickListener {
            findNavController().navigateUp()
        }

        // "Create Alarm" button action
        binding.createAlarmButton.setOnClickListener {
            scheduleAlarmsAndNavigateHome()
        }
    }

    /**
     * Creates Alarm objects, schedules them, shows a confirmation, and navigates to the home screen.
     */
    // ... (imports and class definition)

    /**
     * Creates Alarm objects, schedules them, shows a confirmation, and navigates to the home screen.
     */private fun scheduleAlarmsAndNavigateHome() {
        // Create a list of alarms to be scheduled.
        val alarmsToSchedule = mutableListOf<Alarm>()

        // Loop through each selected day and time to create individual Alarm objects.
        args.selectedDays.forEach { day ->
            args.alarmTimes.forEach { time ->
                // The unique ID for each alarm can be generated based on its properties
                // to ensure it's unique.
                val alarmId = (args.medicineName + day + time).hashCode()

                val newAlarm = Alarm(
                    id = alarmId,
                    medicineName = args.medicineName,
                    medicineType = args.medicineType,
                    dosage = args.dosage,
                    day = day,
                    hour = time.split(":")[0].toInt(),
                    minute = time.split(":")[1].toInt()

                )
                alarmsToSchedule.add(newAlarm)
            }
        }

        // Schedule each created alarm.
        alarmsToSchedule.forEach { alarm ->
            alarmScheduler.schedule(alarm)
        }

        // Show a confirmation message to the user.
        Toast.makeText(requireContext(), "Alarm(s) Created Successfully!", Toast.LENGTH_SHORT).show()

        // Navigate back to the HomeFragment and clear all screens in between.
        findNavController().popBackStack(R.id.homeFragment, false)
    }

// ... (rest of the class)



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
