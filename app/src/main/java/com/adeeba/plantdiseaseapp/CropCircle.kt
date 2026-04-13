package com.adeeba.plantdiseaseapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp

@Composable
fun CropCircle(icon: String, name: String) {

    val colors = MaterialTheme.colorScheme

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(end = 12.dp)
    ) {

        Box(
            modifier = Modifier
                .size(70.dp)
                .background(colors.surfaceVariant, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(icon)
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(name, color = colors.onSurface)
    }
}