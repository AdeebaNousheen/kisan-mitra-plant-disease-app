package com.adeeba.plantdiseaseapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun ToolCard(
    emoji: String,
    title: String,
    onClick: () -> Unit
) {

    val colors = MaterialTheme.colorScheme
    var pressed by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .size(110.dp)
            .graphicsLayer {
                scaleX = if (pressed) 0.95f else 1f
                scaleY = if (pressed) 0.95f else 1f
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                pressed = true
                onClick()
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = colors.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {

        LaunchedEffect(pressed) {
            if (pressed) {
                delay(100)
                pressed = false
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(text = emoji, fontSize = 28.sp)

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                fontSize = 14.sp,
                color = colors.onSurface
            )
        }
    }
}