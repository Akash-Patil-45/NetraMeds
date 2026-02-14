package com.akash.netrameds.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scan_history")
data class ScanHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val medicineName: String,
    val expiryDate: String, // e.g., "MM/YYYY" or "Not Found"
    val scanTimestamp: Long // The exact time of the scan
)