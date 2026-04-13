package com.adeeba.plantdiseaseapp

import androidx.room.Database
import androidx.room.RoomDatabase
import com.adeeba.plantdiseaseapp.entity.*

@Database(
    entities = [
        DetectionEntity::class,
        ChatMessage::class,
        ChatSession::class,
        CropHistory::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun detectionDao(): DetectionDao
    abstract fun chatDao(): ChatDao   // 🔥 THIS MUST EXIST
    abstract fun historyDao(): HistoryDao
}