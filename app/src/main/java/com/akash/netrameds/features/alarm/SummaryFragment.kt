package com.akash.netrameds.features.alarm

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.akash.netrameds.R
import com.akash.netrameds.data.AppDatabase
import com.akash.netrameds.databinding.FragmentSummaryBinding
import com.akash.netrameds.model.Alarm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import android.net.Uri

class SummaryFragment : Fragment() {
    private var _binding: FragmentSummaryBinding? = null
    private val binding get() = _binding!!
    private val args: SummaryFragmentArgs by navArgs()
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
        alarmScheduler = AlarmScheduler(requireContext().applicationContext)
        displaySummary()
        setupClickListeners()
    }

    private fun displaySummary() {
        val summaryBuilder = SpannableStringBuilder()
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
        binding.fullSummaryText.text = summaryBuilder
    }

    private fun setupClickListeners() {
        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        binding.previousButton.setOnClickListener { findNavController().navigateUp() }

        binding.createAlarmButton.setOnClickListener {
            // First, check for the "Draw over other apps" permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(requireContext())) {
                // If it's not granted, ask for it.
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:${requireContext().packageName}")
                )
                startActivity(intent)
                Toast.makeText(requireContext(), "Please grant 'Draw over other apps' permission", Toast.LENGTH_LONG).show()

            } else {
                // If the first permission is granted, now check for the "Exact Alarm" permission.
                if (canScheduleExactAlarms()) {
                    // Both permissions are granted, schedule the alarm.
                    scheduleAlarmsAndNavigateHome()
                } else {
                    // --- THIS IS THE MISSING LOGIC ---
                    // The exact alarm permission is denied, so ask for it.
                    Intent().apply {
                        action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                    }.also {
                        startActivity(it)
                    }
                    Toast.makeText(requireContext(), "Please grant 'Alarms & reminders' permission", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // --- ADD THIS HELPER FUNCTION ---
    private fun canScheduleExactAlarms(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
            return alarmManager.canScheduleExactAlarms()
        }
        return true // Permission is not needed for older versions
    }

    private fun scheduleAlarmsAndNavigateHome() {
        lifecycleScope.launch(Dispatchers.IO) {
            val alarmDao = AppDatabase.getDatabase(requireContext()).alarmDao()
            args.selectedDays.forEach { day ->
                args.alarmTimes.forEach { timeString ->
                    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
                    val date = sdf.parse(timeString)
                    val cal = Calendar.getInstance().apply { timeInMillis = date.time }
                    val hour24 = cal.get(Calendar.HOUR_OF_DAY)
                    val minute = cal.get(Calendar.MINUTE)
                    val newAlarm = Alarm(
                        medicineName = args.medicineName,
                        medicineType = args.medicineType,
                        dosage = args.dosage,
                        day = day,
                        hour = hour24,
                        minute = minute
                    )
                    val generatedId = alarmDao.insert(newAlarm).toInt()
                    val alarmToSchedule = newAlarm.copy(id = generatedId)
                    alarmScheduler.schedule(alarmToSchedule)
                }
            }
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "Alarm(s) Created Successfully!", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack(R.id.homeFragment, false)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}