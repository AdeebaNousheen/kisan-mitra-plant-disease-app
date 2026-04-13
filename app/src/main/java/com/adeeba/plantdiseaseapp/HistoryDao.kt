package com.adeeba.plantdiseaseapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.adeeba.plantdiseaseapp.entity.CropHistory

@Dao
interface HistoryDao {

    @Insert
    suspend fun insert(history: CropHistory)

    @Query("SELECT * FROM crop_history ORDER BY date DESC")
    suspend fun getAll(): List<CropHistory>
}