package com.akash.netrameds.features.alarm

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import com.akash.netrameds.R
import com.akash.netrameds.model.Alarm

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // Always reschedule the next alarm first
        rescheduleNextAlarm(context, intent)

        // Check for the "Draw over other apps" permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(context)) {
            // --- PERMISSION GRANTED: Launch activity directly ---
            Log.d("AlarmReceiver", "Permission granted. Launching activity directly.")
            val directIntent = Intent(context, AlarmActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                if (intent.extras != null) {
                    putExtras(intent.extras!!)
                }
            }
            context.startActivity(directIntent)
        } else {
            // --- PERMISSION DENIED: Fallback to a notification ---
            Log.d("AlarmReceiver", "Permission denied. Falling back to full-screen notification.")

            val medicineName = intent.getStringExtra("EXTRA_MEDICINE_NAME") ?: "Medicine"
            val dosage = intent.getStringExtra("EXTRA_DOSAGE") ?: ""
            val medicineType = intent.getStringExtra("EXTRA_MEDICINE_TYPE") ?: ""
            val notificationId = System.currentTimeMillis().toInt()

            val notificationIntent = Intent(context, AlarmActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("EXTRA_MEDICINE_NAME", medicineName)
                putExtra("EXTRA_DOSAGE", dosage)
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                notificationId,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val notification = NotificationCompat.Builder(context, "medicine_reminder_channel")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Time for your medication!")
                .setContentText("Take $dosage of $medicineName ($medicineType)")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setFullScreenIntent(pendingIntent, true) // Use the standard full-screen intent here
                .build()

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(notificationId, notification)
        }
    }

    private fun rescheduleNextAlarm(context: Context, intent: Intent) {
        val alarmId = intent.getIntExtra("EXTRA_ALARM_ID", -1)
        if (alarmId == -1) {
            Log.e("AlarmReceiver", "Invalid alarmId, cannot reschedule.")
            return
        }
        val alarmToReschedule = Alarm(
            id = alarmId,
            medicineName = intent.getStringExtra("EXTRA_MEDICINE_NAME") ?: "",
            medicineType = intent.getStringExtra("EXTRA_MEDICINE_TYPE") ?: "",
            dosage = intent.getStringExtra("EXTRA_DOSAGE") ?: "",
            day = intent.getStringExtra("EXTRA_DAY") ?: "",
            hour = intent.getIntExtra("EXTRA_HOUR", 0),
            minute = intent.getIntExtra("EXTRA_MINUTE", 0)
        )
        val scheduler = AlarmScheduler(context)
        scheduler.schedule(alarmToReschedule)
        Log.d("AlarmReceiver", "Rescheduled alarm ID: $alarmId")
    }
}