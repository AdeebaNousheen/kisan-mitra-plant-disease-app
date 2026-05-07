package com.adeeba.plantdiseaseapp

data class Post(
    val id: String = "",
    val userId: String = "",
    val username: String = "",
    val message: String = "",
    val imageUrl: String = "",
    val likes: Int = 0,
    val likedBy: List<String> = emptyList(),
    val profileUrl: String = "",
    val bio: String = "",
    val timestamp: Long = 0,
    val bestReplyId: String = ""   // 🔥 NEW
)