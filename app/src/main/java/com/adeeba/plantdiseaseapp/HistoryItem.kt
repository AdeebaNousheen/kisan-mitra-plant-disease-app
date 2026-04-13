package com.adeeba.plantdiseaseapp

data class HistoryItem(
    val name: String,
    val confidence: Double,
    val description: String,
    val treatment: String,
    val prevention: String
)