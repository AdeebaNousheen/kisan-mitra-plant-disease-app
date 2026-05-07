package com.adeeba.plantdiseaseapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

data class ToolItem(
    val emoji: String,
    val title: String,
    val route: String
)

@Composable
fun ToolsHomeScreen(navController: NavController) {

    val tools = listOf(
        ToolItem("🌱", "Fertilizer", "tool/Fertilizer"),
        ToolItem("💉", "Pesticide", "tool/Pesticide"),
        ToolItem("🚜", "Farming", "tool/Farming")
    )

    Column(modifier = Modifier.padding(16.dp)) {

        // 🔥 HEADER
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Tools", style = MaterialTheme.typography.titleLarge)
            TextButton(onClick = { /* future: open full tools page */ }) {
                Text("View All")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 🔥 TOOL LIST
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(tools) { tool ->

                ToolCard(
                    emoji = tool.emoji,
                    title = tool.title
                ) {
                    navController.navigate(tool.route)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 🔥 PROFESSIONAL ADD-ON
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                Text("🤖 Smart Tip",
                    style = MaterialTheme.typography.titleMedium)

                Spacer(modifier = Modifier.height(6.dp))

                Text("Use fertilizer based on soil moisture and crop type.")
            }
        }
    }
}