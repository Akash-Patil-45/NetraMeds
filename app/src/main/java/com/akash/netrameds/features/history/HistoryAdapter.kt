package com.akash.netrameds.features.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.akash.netrameds.databinding.ListItemScanHistoryBinding
import com.akash.netrameds.model.ScanHistory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryAdapter : ListAdapter<ScanHistory, HistoryAdapter.HistoryViewHolder>(DiffCallback()) {

    // Formatter for the date and time
    private val sdf = SimpleDateFormat("yyyy-MM-dd, hh:mm a", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ListItemScanHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, sdf)
    }

    class HistoryViewHolder(private val binding: ListItemScanHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ScanHistory, sdf: SimpleDateFormat) {
            binding.medicineNameText.text = item.medicineName
            binding.expiryDateText.text = "Exp: ${item.expiryDate}"

            val scanDate = sdf.format(Date(item.scanTimestamp))
            binding.scanDateText.text = "Scanned: $scanDate"
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ScanHistory>() {
        override fun areItemsTheSame(oldItem: ScanHistory, newItem: ScanHistory): Boolean {
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: ScanHistory, newItem: ScanHistory): Boolean {
            return oldItem == newItem
        }
    }
}