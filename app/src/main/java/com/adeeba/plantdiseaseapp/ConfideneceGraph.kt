package com.adeeba.plantdiseaseapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import com.adeeba.plantdiseaseapp.entity.DetectionEntity

@Composable
fun ConfidenceGraph(history: List<DetectionEntity>) {

    val maxConfidence = 100f

    Column {

        Text("Confidence Trend")

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {

            history.takeLast(5).forEach { item ->

                val percent =
                    if (item.confidence > 1)
                        item.confidence.toFloat()
                    else
                        (item.confidence * 100).toFloat()

                val color = when {
                    percent >= 70 -> Color(0xFF4CAF50)
                    percent >= 40 -> Color(0xFFFF9800)
                    else -> Color(0xFFF44336)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    Box(
                        modifier = Modifier
                            .width(20.dp)
                            .height((percent / maxConfidence * 150).dp)
                            .background(color, RoundedCornerShape(6.dp))
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text("${percent.toInt()}%")
                }
            }
        }
    }
}