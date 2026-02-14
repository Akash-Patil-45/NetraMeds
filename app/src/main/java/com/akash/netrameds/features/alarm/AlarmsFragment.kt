package com.akash.netrameds.features.alarms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.akash.netrameds.R
import com.akash.netrameds.data.AppDatabase
import com.akash.netrameds.data.AlarmDao
import com.akash.netrameds.databinding.FragmentAllAlarmsBinding
import com.akash.netrameds.features.alarm.AlarmScheduler
import com.akash.netrameds.features.home.UpcomingAlarmsAdapter // <-- Reusing this adapter
import com.akash.netrameds.model.Alarm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlarmsFragment : Fragment() {

    private var _binding: FragmentAllAlarmsBinding? = null
    private val binding get() = _binding!!

    private lateinit var alarmDao: AlarmDao
    private lateinit var alarmScheduler: AlarmScheduler
    private lateinit var manageAlarmsAdapter: UpcomingAlarmsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllAlarmsBinding.inflate(inflater, container, false)
        alarmDao = AppDatabase.getDatabase(requireContext()).alarmDao()
        alarmScheduler = AlarmScheduler(requireContext().applicationContext)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        setupRecyclerView()
        loadAllAlarms()
    }

    private fun setupRecyclerView() {
        manageAlarmsAdapter = UpcomingAlarmsAdapter(
            onEditClick = { alarm ->
                // The simplest "Edit" is to delete the old alarm and start the create flow
                showEditConfirmationDialog(alarm)
            },
            onCancelClick = { alarm ->
                showCancelConfirmationDialog(alarm)
            }
        )
        binding.allAlarmsRecyclerview.adapter = manageAlarmsAdapter
    }

    private fun loadAllAlarms() {
        lifecycleScope.launch(Dispatchers.IO) {
            val allAlarms = alarmDao.getAllAlarms() // Get ALL alarms
            withContext(Dispatchers.Main) {
                binding.noAlarmsText.isVisible = allAlarms.isEmpty()
                binding.allAlarmsRecyclerview.isVisible = allAlarms.isNotEmpty()
                manageAlarmsAdapter.submitList(allAlarms)
            }
        }
    }

    private fun showCancelConfirmationDialog(alarm: Alarm) {
        AlertDialog.Builder(requireContext())
            .setTitle("Cancel Alarm")
            .setMessage("Are you sure you want to cancel this alarm?")
            .setPositiveButton("Yes") { _, _ -> cancelAlarm(alarm) }
            .setNegativeButton("No", null)
            .show()
    }

    private fun showEditConfirmationDialog(alarm: Alarm) {
        AlertDialog.Builder(requireContext())
            .setTitle("Edit Alarm")
            .setMessage("To edit this alarm, the old one will be canceled. You will then be guided to create a new one.")
            .setPositiveButton("Continue") { _, _ ->
                cancelAlarm(alarm)
                // Navigate to the start of the alarm creation flow
                findNavController().navigate(R.id.action_alarmsFragment_to_createAlarmFragment)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun cancelAlarm(alarm: Alarm) {
        lifecycleScope.launch(Dispatchers.IO) {
            alarmScheduler.cancel(alarm)
            alarmDao.delete(alarm)
            // Refresh the list from the database
            loadAllAlarms()
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "Alarm Canceled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}