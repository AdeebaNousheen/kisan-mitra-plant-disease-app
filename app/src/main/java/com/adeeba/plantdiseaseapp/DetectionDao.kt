package com.adeeba.plantdiseaseapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.adeeba.plantdiseaseapp.entity.DetectionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DetectionDao {

    // ✅ INSERT (SAFE - avoids duplicates crash)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDetection(detection: DetectionEntity)

    // ✅ FLOW (REAL-TIME UPDATES)
    @Query("SELECT * FROM detection_history ORDER BY id DESC")
    fun getAllDetections(): Flow<List<DetectionEntity>>

    // ✅ CLEAR ALL
    @Query("DELETE FROM detection_history")
    suspend fun clearAll()

    // ✅ DELETE ONE
    @Query("DELETE FROM detection_history WHERE id = :id")
    suspend fun deleteDetection(id: Int)
}