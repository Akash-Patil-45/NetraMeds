package com.akash.netrameds.features.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.akash.netrameds.model.Alarm
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class AlarmScheduler(private val context: Context) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun schedule(alarm: Alarm) {
        // 1. Pass ALL alarm details so the receiver can reschedule the next alarm.
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("EXTRA_ALARM_ID", alarm.id)
            putExtra("EXTRA_MEDICINE_NAME", alarm.medicineName)
            putExtra("EXTRA_MEDICINE_TYPE", alarm.medicineType)
            putExtra("EXTRA_DOSAGE", alarm.dosage)
            putExtra("EXTRA_DAY", alarm.day) // This is the day OR the date
            putExtra("EXTRA_HOUR", alarm.hour)
            putExtra("EXTRA_MINUTE", alarm.minute)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance()

        // --- NEW LOGIC: Check if it's a date or a repeating day ---
        if (alarm.day.contains("-")) {
            // --- IT'S A SPECIFIC DATE (e.g., "2025-10-30") ---
            try {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                sdf.timeZone = TimeZone.getTimeZone("UTC")
                val date: Date = sdf.parse(alarm.day) ?: Date()
                calendar.time = date

                // Set the specific time from the user
                calendar.set(Calendar.HOUR_OF_DAY, alarm.hour)
                calendar.set(Calendar.MINUTE, alarm.minute)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)

                if (calendar.timeInMillis < System.currentTimeMillis()) {
                    Log.w("AlarmScheduler", "One-time alarm set for a time in the past.")
                    // Note: You might want to handle this case, e.g., by not setting the alarm
                }

                // Set a ONE-TIME exact alarm
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
                Log.d("AlarmScheduler", "Scheduled ONE-TIME alarm for ${calendar.time}")

            } catch (e: Exception) {
                Log.e("AlarmScheduler", "Error parsing date: ${alarm.day}", e)
            }

        } else {
            // --- IT'S A REPEATING DAY OF THE WEEK (e.g., "Mon") ---
            calendar.set(Calendar.HOUR_OF_DAY, alarm.hour)
            calendar.set(Calendar.MINUTE, alarm.minute)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            calendar.set(Calendar.DAY_OF_WEEK, getCalendarDay(alarm.day))

            // If time is in the past for this week, set it for next week
            if (calendar.timeInMillis < System.currentTimeMillis()) {
                calendar.add(Calendar.WEEK_OF_YEAR, 1)
            }

            // Set a one-time alarm that will be rescheduled by the receiver
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
            Log.d("AlarmScheduler", "Scheduled REPEATING alarm for ${calendar.time}")
        }
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

    private fun getCalendarDay(day: String): Int {
        return when (day.lowercase(Locale.getDefault())) {
            "sun" -> Calendar.SUNDAY
            "mon" -> Calendar.MONDAY
            "tue" -> Calendar.TUESDAY
            "wed" -> Calendar.WEDNESDAY
            "thu" -> Calendar.THURSDAY
            "fri" -> Calendar.FRIDAY
            "sat" -> Calendar.SATURDAY
            else -> -1
        }
    }
}