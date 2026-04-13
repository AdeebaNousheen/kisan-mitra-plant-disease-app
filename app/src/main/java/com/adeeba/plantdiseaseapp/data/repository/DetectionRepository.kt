package com.adeeba.plantdiseaseapp.data.repository

import android.content.Context
import com.adeeba.plantdiseaseapp.data.local.AppDatabase
import com.adeeba.plantdiseaseapp.data.local.entity.DetectionEntity
import kotlinx.coroutines.flow.Flow

class DetectionRepository(context: Context) {

    private val detectionDao =
        AppDatabase.getDatabase(context).detectionDao()

    suspend fun insertDetection(detection: DetectionEntity) {
        detectionDao.insertDetection(detection)
    }

    fun getAllDetections(): Flow<List<DetectionEntity>> {
        return detectionDao.getAllDetections()
    }

    suspend fun markAsSynced(id: Int) {
        detectionDao.markAsSynced(id)
    }
    suspend fun deleteDetection(detection: DetectionEntity) {
        detectionDao.deleteDetection(detection)
    }
}