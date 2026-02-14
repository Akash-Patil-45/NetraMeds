package com.akash.netrameds.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.akash.netrameds.model.Alarm
import com.akash.netrameds.model.ScanHistory

// 1. Add ScanHistory::class to the entities array
// 2. Increment the version number to 2
@Database(entities = [Alarm::class, ScanHistory::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun alarmDao(): AlarmDao
    abstract fun scanHistoryDao(): ScanHistoryDao // 3. Add the new DAO abstract function

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "alarm_database"
                )
                    // 4. Add a migration strategy.
                    // This one is easy for development but will clear all user data.
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}