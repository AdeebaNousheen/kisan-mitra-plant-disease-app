package com.adeeba.plantdiseaseapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.adeeba.plantdiseaseapp.data.local.entity.DetectionEntity
import com.adeeba.plantdiseaseapp.data.repository.DetectionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DetectionViewModel(application: Application) :
    AndroidViewModel(application) {

    private val repository = DetectionRepository(application)

    val detections = repository.getAllDetections()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    // Existing insert function (used internally)
    fun insertDetection(detection: DetectionEntity) {
        viewModelScope.launch {
            repository.insertDetection(detection)
        }
    }

    // NEW function used by MainActivity
    fun insertDetection(
        plantName: String,
        diseaseName: String,
        confidence: Float,
        imagePath: String
    ) {

        viewModelScope.launch {

            val detection = DetectionEntity(
                plantName = plantName,
                diseaseName = diseaseName,
                confidence = confidence,
                imagePath = imagePath
            )

            repository.insertDetection(detection)
        }
    }

    fun markAsSynced(id: Int) {
        viewModelScope.launch {
            repository.markAsSynced(id)
        }
    }

    fun insertTestData() {
        insertDetection(
            DetectionEntity(
                plantName = "Tomato",
                diseaseName = "Leaf Spot",
                confidence = 0.92f,
                imagePath = ""
            )
        )
    }

    fun deleteDetection(detection: DetectionEntity) {
        viewModelScope.launch {
            repository.deleteDetection(detection)
        }
    }
}