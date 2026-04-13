package com.adeeba.plantdiseaseapp

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// 🌞 LIGHT (your UI)
private val LightColors = lightColorScheme(
    primary = Color(0xFF6C63FF),
    secondary = Color(0xFF6BCB77),
    background = Color(0xFFF5F5F5),
    surface = Color.White,
    surfaceVariant = Color(0xFFEAEAEA),
    onPrimary = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black
)

// 🌙 DARK (matched version)
private val DarkColors = darkColorScheme(
    primary = Color(0xFF8E97FD),
    secondary = Color(0xFF4CAF50),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    surfaceVariant = Color(0xFF2C2C2C),
    onPrimary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {

    val isDark = ThemeManager.isDarkMode.value

    MaterialTheme(
        colorScheme = if (isDark) DarkColors else LightColors,
        content = content
    )
}