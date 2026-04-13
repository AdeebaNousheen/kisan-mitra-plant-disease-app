package com.adeeba.plantdiseaseapp

import com.adeeba.plantdiseaseapp.entity.DetectionEntity
import kotlinx.coroutines.flow.Flow

class DetectionRepository(private val dao: DetectionDao) {

    suspend fun insert(detection: DetectionEntity) {
        dao.insertDetection(detection)
    }

    fun getAll(): Flow<List<DetectionEntity>> {
        return dao.getAllDetections()
    }
}