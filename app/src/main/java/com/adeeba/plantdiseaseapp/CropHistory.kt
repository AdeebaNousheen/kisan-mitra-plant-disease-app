package com.adeeba.plantdiseaseapp.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "crop_history")
data class CropHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val crop: String,
    val disease: String,
    val remedy: String,
    val confidence: String,
    val date: Long = System.currentTimeMillis()
)