package com.adeeba.plantdiseaseapp

import android.content.Context

object CropStorage {

    fun saveCrops(context: Context, crops: List<String>) {
        val prefs = context.getSharedPreferences("crops", Context.MODE_PRIVATE)
        prefs.edit().putString("my_crops", crops.joinToString(",")).apply()
    }

    fun getCrops(context: Context): List<String> {
        val prefs = context.getSharedPreferences("crops", Context.MODE_PRIVATE)
        return prefs.getString("my_crops", "")!!
            .split(",")
            .filter { it.isNotEmpty() }
    }
}