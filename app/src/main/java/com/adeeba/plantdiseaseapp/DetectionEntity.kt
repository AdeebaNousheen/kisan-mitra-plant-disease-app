package com.adeeba.plantdiseaseapp.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "detection_history")
data class DetectionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val confidence: Double,
    val description: String,
    val treatment: String,
    val prevention: String,
    val imagePath: String, // ✅ NEW
    val timestamp: Long = System.currentTimeMillis() // ✅ NEW
)