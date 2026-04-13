package com.adeeba.plantdiseaseapp

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.rememberDismissState
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.adeeba.plantdiseaseapp.entity.DetectionEntity
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProfileScreen(
    onItemClick: (String, Double, String, String, String) -> Unit
) {

    val context = LocalContext.current
    val dao = DatabaseProvider.getDatabase(context).detectionDao()
    val scope = rememberCoroutineScope()

    // ✅ FLOW → STATE (AUTO UPDATE)
    val historyList by dao.getAllDetections().collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text("Scan History", style = MaterialTheme.typography.h6)

        Spacer(modifier = Modifier.height(12.dp))

        // 🔥 CLEAR HISTORY
        Button(
            onClick = {
                scope.launch {
                    dao.clearAll()
                }
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
        ) {
            Text("Clear History", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (historyList.isEmpty()) {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No scans yet")
            }

        } else {

            LazyColumn {

                items(historyList, key = { it.id }) { item ->

                    val dismissState = rememberDismissState { dismissValue ->

                        if (dismissValue == DismissValue.DismissedToStart) {
                            scope.launch {
                                dao.deleteDetection(item.id)
                            }
                        }
                        true
                    }

                    SwipeToDismiss(
                        state = dismissState,
                        directions = setOf(DismissDirection.EndToStart),

                        background = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(end = 20.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Text("Delete", color = Color.Red)
                            }
                        }
                    ) {

                        // ✅ SAFE CONFIDENCE CALCULATION
                        val confidencePercent =
                            if (item.confidence > 1)
                                item.confidence.toInt()
                            else
                                (item.confidence * 100).toInt()

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clickable {
                                    onItemClick(
                                        item.name,
                                        item.confidence,
                                        item.description,
                                        item.treatment,
                                        item.prevention
                                    )
                                }
                                .animateContentSize(),
                            elevation = 8.dp,
                            shape = RoundedCornerShape(16.dp)
                        ) {

                            Column(modifier = Modifier.padding(12.dp)) {

                                Text(
                                    item.name.ifBlank { "Unknown Disease" }, // ✅ SAFETY
                                    style = MaterialTheme.typography.h6
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text("Confidence: $confidencePercent%")

                                Spacer(modifier = Modifier.height(6.dp))

                                LinearProgressIndicator(
                                    progress = (confidencePercent / 100f).coerceIn(0f, 1f), // ✅ SAFE RANGE
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