package com.adeeba.plantdiseaseapp

data class WeatherResponse(
    val current_condition: List<CurrentCondition>
)

data class CurrentCondition(
    val temp_C: String,
    val weatherDesc: List<WeatherDesc>
)

data class WeatherDesc(
    val value: String
)