package com.adeeba.plantdiseaseapp

import androidx.compose.runtime.mutableStateOf

object ThemeManager {
    var isDarkMode = mutableStateOf(false)

    fun toggle() {
        isDarkMode.value = !isDarkMode.value
    }
}