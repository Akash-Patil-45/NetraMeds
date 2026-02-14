package com.akash.netrameds.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.akash.netrameds.model.ScanHistory

@Dao
interface ScanHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(historyItem: ScanHistory)

    // Gets all history items, with the most recent scan first
    @Query("SELECT * FROM scan_history ORDER BY scanTimestamp DESC")
    suspend fun getAllScanHistory(): List<ScanHistory>
}