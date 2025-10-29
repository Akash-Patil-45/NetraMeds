// In package com.akash.netrameds.model

package com.akash.netrameds.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarms")
data class Alarm(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // Auto-generating ID is best practice
    val medicineName: String,
    val medicineType: String,
    val dosage: String,
    val day: String, // e.g., "Mon", "Tue"
    val hour: Int,   // 24-hour format
    val minute: Int
)