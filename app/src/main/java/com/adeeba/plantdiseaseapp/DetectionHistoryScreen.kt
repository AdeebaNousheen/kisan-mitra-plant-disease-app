package com.adeeba.plantdiseaseapp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.adeeba.plantdiseaseapp.entity.DetectionEntity

@Composable
fun DetectionHistoryScreen(
    onItemClick: (DetectionEntity) -> Unit,
    onBack: () -> Unit
) {

    val context = LocalContext.current
    val dao = DatabaseProvider.getDatabase(context).detectionDao()

    val history by dao.getAllDetections().collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // 🔙 BACK
        TextButton(onClick = onBack) {
            Text("← Back")
        }

        Spacer(Modifier.height(8.dp))

        Text(
            "Scan History",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(12.dp))

        // ✅ ADDED GRAPH (NO CHANGE TO YOUR CODE)
        if (history.isNotEmpty()) {
            ConfidenceGraph(history)
            Spacer(Modifier.height(16.dp))
        }

        if (history.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No scans yet")
            }
        } else {

            LazyColumn {

                items(history) { item ->

                    val confidencePercent =
                        if (item.confidence > 1)
                            item.confidence.toInt()
                        else
                            (item.confidence * 100).toInt()

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable {
                                onItemClick(item)
                            },
                        elevation = CardDefaults.cardElevation(6.dp)
                    ) {

                        Row(
                            modifier = Modifier.padding(12.dp)
                        ) {

                            // ✅ ADDED IMAGE (NEW FEATURE)
                            AsyncImage(
                                model = item.imagePath,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(80.dp)
                                    .background(Color.LightGray, RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {

                                Text(
                                    text = item.name,
                                    style = MaterialTheme.typography.titleLarge
                                )

                                Spacer(Modifier.height(6.dp))

                                Text("Confidence: $confidencePercent%")

                                Spacer(Modifier.height(6.dp))

                                LinearProgressIndicator(
                                    progress = confidencePercent / 100f,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}