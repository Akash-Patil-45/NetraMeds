// In D:/NetraMeds/app/src/main/java/com/akash/netrameds/model/Alarm.kt

package com.akash.netrameds.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarms")
data class Alarm(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val medicineName: String,
    val medicineType: String,
    val dosage: String,

    val day: String, // <-- ADD THIS LINE
    val hour: Int,
    val minute: Int
    // You can add other fields here if needed, like a list of days for the alarm to repeat
)
    