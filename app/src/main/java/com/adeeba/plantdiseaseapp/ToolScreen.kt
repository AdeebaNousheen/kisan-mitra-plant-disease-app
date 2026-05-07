package com.adeeba.plantdiseaseapp

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolScreen(
    toolName: String,
    onBack: () -> Unit
) {

    when (toolName.lowercase()) {

        "fertilizer" -> {
            FertilizerScreen(onBack)
        }

        "pesticide" -> {
            PesticideScreen(onBack)
        }

        "farming" -> {
            FarmingScreen(onBack)
        }

        else -> {
            SimpleMessageScreen("Tool not found", onBack)
        }
    }
}

// ================================
// 📌 FALLBACK SCREEN (NO ERROR NOW)
// ================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleMessageScreen(
    message: String,
    onBack: () -> Unit
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Info") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = message)
        }
    }
}