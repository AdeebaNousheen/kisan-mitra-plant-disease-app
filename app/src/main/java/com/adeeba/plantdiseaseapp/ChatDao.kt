package com.adeeba.plantdiseaseapp

import androidx.room.*
import com.adeeba.plantdiseaseapp.entity.ChatMessage
import com.adeeba.plantdiseaseapp.entity.ChatSession

@Dao
interface ChatDao {

    // 🔹 Insert session
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: ChatSession): Long

    // 🔹 Insert message
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage)

    // 🔹 Get messages
    @Query("SELECT * FROM ChatMessage WHERE chatId = :chatId ORDER BY id ASC")
    suspend fun getMessages(chatId: Long): List<ChatMessage>

    // 🔹 Get ALL sessions (🔥 FIX NAME)
    @Query("SELECT * FROM ChatSession ORDER BY id DESC")
    suspend fun getAllSessions(): List<ChatSession>

    // 🔹 Update title
    @Query("UPDATE ChatSession SET title = :title WHERE id = :chatId")
    suspend fun updateSessionTitle(chatId: Long, title: String)

    // 🔥 DELETE ALL MESSAGES OF SESSION
    @Query("DELETE FROM ChatMessage WHERE chatId = :chatId")
    suspend fun deleteMessages(chatId: Long)

    // 🔥 DELETE SESSION
    @Query("DELETE FROM ChatSession WHERE id = :chatId")
    suspend fun deleteSession(chatId: Long)
}