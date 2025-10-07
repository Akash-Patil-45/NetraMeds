// In D:/NetraMeds/app/src/main/java/com/akash/netrameds/features/alarm/AlarmReceiver.kt

package com.akash.netrameds.features.alarm

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.akash.netrameds.MainActivity // Assuming MainActivity is your entry point
import com.akash.netrameds.R // Make sure you have your app's R file imported

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // Retrieve data from the intent
        val medicineName = intent.getStringExtra("EXTRA_MEDICINE_NAME") ?: "Medicine"
        val dosage = intent.getStringExtra("EXTRA_DOSAGE") ?: ""
        val medicineType = intent.getStringExtra("EXTRA_MEDICINE_TYPE") ?: ""

        // A unique ID for the notification
        val notificationId = System.currentTimeMillis().toInt()

        // Create an intent to launch the app when the notification is tapped
        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build the notification
        val notification = NotificationCompat.Builder(context, "medicine_reminder_channel") // You need to create this channel
            .setSmallIcon(R.drawable.ic_notification) // Make sure you have this drawable
            .setContentTitle("Time for your medication!")
            .setContentText("Take $dosage of $medicineName ($medicineType)")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        // Show the notification
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notification)
    }
}
