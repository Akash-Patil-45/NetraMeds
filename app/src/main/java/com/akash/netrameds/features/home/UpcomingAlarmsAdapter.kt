package com.akash.netrameds.features.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.akash.netrameds.databinding.ListItemUpcomingAlarmBinding
import com.akash.netrameds.model.Alarm
import java.util.Locale

// Using ListAdapter for better performance with DiffUtil
class UpcomingAlarmsAdapter(
    private val onEditClick: (Alarm) -> Unit,
    private val onCancelClick: (Alarm) -> Unit
) : ListAdapter<Alarm, UpcomingAlarmsAdapter.AlarmViewHolder>(AlarmDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val binding = ListItemUpcomingAlarmBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AlarmViewHolder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val alarm = getItem(position)
        holder.bind(alarm, onEditClick, onCancelClick)
    }

    class AlarmViewHolder(
        private val binding: ListItemUpcomingAlarmBinding,
        private val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            alarm: Alarm,
            onEditClick: (Alarm) -> Unit,
            onCancelClick: (Alarm) -> Unit
        ) {
            // Format data for display
            val title = if (alarm.day.contains("-")) {
                "One-time: ${alarm.day}" // Specific Date
            } else {
                "${alarm.medicineName} (${alarm.day})" // Repeating Day
            }

            // Format time from 24-hour to 12-hour AM/PM
            val hour = alarm.hour
            val minute = alarm.minute
            val amPm = if (hour < 12) "AM" else "PM"
            val displayHour = when {
                hour == 0 -> 12
                hour > 12 -> hour - 12
                else -> hour
            }
            val timeString = String.format(Locale.getDefault(), "%d:%02d %s", displayHour, minute, amPm)

            binding.medicineNameText.text = title
            binding.medicineDetailsText.text = "${alarm.dosage} • $timeString"

            binding.editButton.setOnClickListener { onEditClick(alarm) }
            binding.cancelButton.setOnClickListener { onCancelClick(alarm) }
        }
    }

    // DiffUtil helps the adapter efficiently update only the items that have changed
    class AlarmDiffCallback : DiffUtil.ItemCallback<Alarm>() {
        override fun areItemsTheSame(oldItem: Alarm, newItem: Alarm): Boolean {
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: Alarm, newItem: Alarm): Boolean {
            return oldItem == newItem
        }
    }
}