package com.adeeba.plantdiseaseapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "detection_table")
data class DetectionEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val plantName: String,

    val diseaseName: String,

    val confidence: Float,

    val imagePath: String,   // Path of captured image

    val date: Long = System.currentTimeMillis(),

    val isSynced: Boolean = false
)