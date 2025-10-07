// In D:/NetraMeds/app/src/main/java/com/akash/netrameds/features/alarm/AlarmScheduler.kt

package com.akash.netrameds.features.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.akash.netrameds.model.Alarm
import java.util.Calendar

class AlarmScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun schedule(alarm: Alarm) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("EXTRA_MEDICINE_NAME", alarm.medicineName)
            putExtra("EXTRA_MEDICINE_TYPE", alarm.medicineType)
            putExtra("EXTRA_DOSAGE", alarm.dosage)
        }

        // Each PendingIntent needs a unique request code
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Set the alarm
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, alarm.hour)
            set(Calendar.MINUTE, alarm.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            // If the time has already passed for today, schedule it for the next day
            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1)
            }
        }

        // Set the repeating alarm
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY, // Repeats every day, you might adjust this based on 'alarm.days'
            pendingIntent
        )
    }

    fun cancel(alarm: Alarm) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}
