// In a new package e.g., com.akash.netrameds.data

package com.akash.netrameds.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.akash.netrameds.model.Alarm

@Dao
interface AlarmDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alarm: Alarm): Long // Returns the ID of the new alarm

    @Delete
    suspend fun delete(alarm: Alarm)

    @Query("SELECT * FROM alarms ORDER BY hour, minute")
    suspend fun getAllAlarms(): List<Alarm>
}