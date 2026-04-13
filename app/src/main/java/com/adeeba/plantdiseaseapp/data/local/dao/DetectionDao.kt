package com.adeeba.plantdiseaseapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete
import com.adeeba.plantdiseaseapp.data.local.entity.DetectionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DetectionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDetection(detection: DetectionEntity)

    @Query("SELECT * FROM detection_table ORDER BY date DESC")
    fun getAllDetections(): Flow<List<DetectionEntity>>

    @Query("UPDATE detection_table SET isSynced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: Int)

    @Delete
    suspend fun deleteDetection(detection: DetectionEntity)
}