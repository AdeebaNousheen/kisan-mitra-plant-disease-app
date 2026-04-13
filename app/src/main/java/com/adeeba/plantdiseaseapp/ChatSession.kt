package com.adeeba.plantdiseaseapp.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ChatSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String = "",
    val timestamp: Long = System.currentTimeMillis()
)