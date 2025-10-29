package com.akash.netrameds.features.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.akash.netrameds.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val scheduler = AlarmScheduler(context)
            val dao = AppDatabase.getDatabase(context).alarmDao()

            CoroutineScope(Dispatchers.IO).launch {
                dao.getAllAlarms().forEach { alarm ->
                    scheduler.schedule(alarm)
                }
            }
        }
    }
}