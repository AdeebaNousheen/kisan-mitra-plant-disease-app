package com.adeeba.plantdiseaseapp

import retrofit2.http.GET

interface WeatherApi {

    @GET("Delhi?format=j1")
    suspend fun getWeather(): WeatherResponse
}