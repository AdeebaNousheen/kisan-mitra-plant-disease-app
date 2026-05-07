package com.adeeba.plantdiseaseapp

data class Reply(
    val id: String = "",
    val userId: String = "",
    val username: String = "",
    val message: String = "",
    val likes: Int = 0,
    val likedBy: List<String> = emptyList(),
    val isBest: Boolean = false,
    val type: String = "user" // "user" or "ai"
)