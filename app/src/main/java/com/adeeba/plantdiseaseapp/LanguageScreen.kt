package com.adeeba.plantdiseaseapp

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LanguageScreen(
    context: Context,
    onContinue: () -> Unit
) {

    var selectedLang by remember {
        mutableStateOf(getLanguage(context).ifEmpty { "english" })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text("Select Language 🌍", fontSize = 24.sp)

        Spacer(modifier = Modifier.height(30.dp))

        LanguageItem("English", "english", selectedLang) { selectedLang = it }
        LanguageItem("हिंदी", "hindi", selectedLang) { selectedLang = it }
        LanguageItem("తెలుగు", "telugu", selectedLang) { selectedLang = it }

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                saveLanguage(context, selectedLang)
                onContinue()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continue")
        }
    }
}

@Composable
fun LanguageItem(
    title: String,
    value: String,
    selected: String,
    onClick: (String) -> Unit
) {
    val isSelected = selected == value

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(
                if (isSelected) Color(0xFF4C6EF5) else Color.LightGray,
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick(value) }
            .padding(16.dp)
    ) {
        Text(
            text = title,
            color = if (isSelected) Color.White else Color.Black
        )
    }
}