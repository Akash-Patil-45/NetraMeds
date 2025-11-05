package com.akash.netrameds.features.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.akash.netrameds.R
import com.akash.netrameds.auth.AuthActivity
import com.akash.netrameds.data.AppDatabase
import com.akash.netrameds.data.AlarmDao
import com.akash.netrameds.databinding.FragmentHomeBinding
import com.akash.netrameds.features.alarm.AlarmScheduler
import com.akash.netrameds.model.Alarm
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat // <-- ADDED IMPORT
import java.util.Calendar // <-- ADDED IMPORT
import java.util.Locale // <-- ADDED IMPORT
import java.util.TimeZone // <-- ADDED IMPORT

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var alarmDao: AlarmDao
    private lateinit var alarmScheduler: AlarmScheduler
    private lateinit var upcomingAlarmsAdapter: UpcomingAlarmsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        alarmDao = AppDatabase.getDatabase(requireContext()).alarmDao()
        alarmScheduler = AlarmScheduler(requireContext().applicationContext)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupClickListeners()
    }

    override fun onResume() {
        super.onResume()
        // Load or refresh alarms every time the user comes to this screen
        loadAlarms()
    }

    // --- THIS FUNCTION IS NOW UPDATED ---
    private fun loadAlarms() {
        lifecycleScope.launch(Dispatchers.IO) {
            // Get current time details
            val now = Calendar.getInstance()
            val currentHour = now.get(Calendar.HOUR_OF_DAY)
            val currentMinute = now.get(Calendar.MINUTE)
            val currentTimeInMinutes = (currentHour * 60) + currentMinute

            // Get current day string (e.g., "Wed")
            val dayOfWeekFormatter = SimpleDateFormat("E", Locale.getDefault())
            val currentDayString = dayOfWeekFormatter.format(now.time)

            // Get current date string (e.g., "2025-11-05")
            val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateFormatter.timeZone = TimeZone.getTimeZone("UTC") // Match the format from the date picker
            val currentDateString = dateFormatter.format(now.time)

            // Get all alarms (already sorted by time from 00:00 to 23:59)
            val allAlarms = alarmDao.getAllAlarms()

            // Filter the alarms in Kotlin
            val upcomingAlarms = allAlarms.filter { alarm ->
                // 1. Check if the alarm is scheduled for today
                val isRepeatingDay = alarm.day.equals(currentDayString, ignoreCase = true)
                val isSpecificDate = alarm.day == currentDateString
                val isForToday = isRepeatingDay || isSpecificDate

                if (!isForToday) return@filter false

                // 2. Check if the alarm time is in the future
                val alarmTimeInMinutes = (alarm.hour * 60) + alarm.minute
                val isUpcomingTime = alarmTimeInMinutes > currentTimeInMinutes

                isUpcomingTime // Keep the alarm if it's for today AND in the future

            }.take(3) // 3. Take only the first 3

            // Update the UI on the main thread
            withContext(Dispatchers.Main) {
                if (upcomingAlarms.isEmpty()) {
                    // Show "No alarms" text and hide the list
                    binding.noAlarmsText.text = "No upcoming alarms today." // Specific message
                    binding.noAlarmsText.visibility = View.VISIBLE
                    binding.upcomingAlarmsRecyclerview.visibility = View.GONE
                } else {
                    // Show the list and hide the "No alarms" text
                    binding.noAlarmsText.visibility = View.GONE
                    binding.upcomingAlarmsRecyclerview.visibility = View.VISIBLE
                    upcomingAlarmsAdapter.submitList(upcomingAlarms)
                }
            }
        }
    }

    private fun setupRecyclerView() {
        upcomingAlarmsAdapter = UpcomingAlarmsAdapter(
            onEditClick = { alarm ->
                Toast.makeText(requireContext(), "Please create a new alarm and cancel the old one.", Toast.LENGTH_LONG).show()
                findNavController().navigate(R.id.action_homeFragment_to_createAlarmFragment)
            },
            onCancelClick = { alarm ->
                showCancelConfirmationDialog(alarm)
            }
        )

        binding.upcomingAlarmsRecyclerview.apply {
            adapter = upcomingAlarmsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupClickListeners() {
        binding.settingsButton.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
        }
        binding.medicineRecognitionCard.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_scanMedicineFragment)
        }
        binding.medicineHistoryCard.setOnClickListener {
            Toast.makeText(requireContext(), "Medicine History clicked!", Toast.LENGTH_SHORT).show()
        }
        binding.smartAlarmCard.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_createAlarmFragment)
        }
        binding.supportCard.setOnClickListener {
            Toast.makeText(requireContext(), "Support clicked!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showCancelConfirmationDialog(alarm: Alarm) {
        AlertDialog.Builder(requireContext())
            .setTitle("Cancel Alarm")
            .setMessage("Are you sure you want to cancel this alarm?")
            .setPositiveButton("Yes") { _, _ ->
                cancelAlarm(alarm)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun cancelAlarm(alarm: Alarm) {
        lifecycleScope.launch(Dispatchers.IO) {
            alarmScheduler.cancel(alarm)
            alarmDao.delete(alarm)

            // Refresh the list on the main thread
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "Alarm canceled", Toast.LENGTH_SHORT).show()
                loadAlarms() // Reload the list
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}