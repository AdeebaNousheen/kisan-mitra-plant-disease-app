package com.adeeba.plantdiseaseapp

data class DiseaseData(
    val name: String = "",
    val confidence: Double = 0.0,
    val description: String = "",
    val treatment: String = "",
    val prevention: String = ""
)