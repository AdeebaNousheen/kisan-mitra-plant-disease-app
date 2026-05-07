package com.adeeba.plantdiseaseapp

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmingScreen(onBack: () -> Unit) {

    var selectedCrop by remember { mutableStateOf("Wheat") }
    var showDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    var plotSize by remember { mutableStateOf(1.0f) }
    var showResult by remember { mutableStateOf(false) }

    val crops = listOf(
        Crop("Wheat", R.drawable.wheat),
        Crop("Rice", R.drawable.rice),
        Crop("Banana", R.drawable.banana),
        Crop("Maize", R.drawable.maize),
        Crop("Apple", R.drawable.apple)
    )

    val filtered = crops.filter {
        it.name.contains(searchQuery, true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Farming Planner") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {

            // 🌱 CROP SELECTOR
            Text("Select Crop", fontWeight = FontWeight.Bold)

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF4CAF50))
                    .clickable { showDialog = true }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                val cropObj = crops.find { it.name == selectedCrop }

                cropObj?.let {
                    Image(
                        painter = painterResource(it.image),
                        contentDescription = null,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(Modifier.width(10.dp))

                Text(
                    selectedCrop,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )

                Icon(Icons.Default.ArrowDropDown, null, tint = Color.White)
            }

            Spacer(Modifier.height(20.dp))

            // 📏 PLOT SIZE
            Text("Plot Size", fontWeight = FontWeight.Bold)

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                IconButton(onClick = {
                    if (plotSize > 0.1f) plotSize -= 0.1f
                }) {
                    Icon(Icons.Default.Remove, null)
                }

                Text(
                    "$plotSize Acre",
                    modifier = Modifier
                        .background(Color(0xFFEDEDED), RoundedCornerShape(12.dp))
                        .padding(16.dp)
                )

                IconButton(onClick = {
                    plotSize += 0.1f
                }) {
                    Icon(Icons.Default.Add, null)
                }
            }

            Spacer(Modifier.height(20.dp))

            // 🔥 BUTTON
            Button(
                onClick = { showResult = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C63FF))
            ) {
                Text("Generate Farming Plan")
            }

            // 📊 RESULT
            if (showResult) {

                Spacer(Modifier.height(20.dp))

                val seeds = plotSize * 20
                val water = plotSize * 1000

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F3F3))
                ) {
                    Column(Modifier.padding(16.dp)) {

                        Text("Crop: $selectedCrop", fontWeight = FontWeight.Bold)

                        Spacer(Modifier.height(10.dp))

                        Text("Smart Farming Plan", fontWeight = FontWeight.Bold)

                        Spacer(Modifier.height(6.dp))

                        Text("• Seeds needed: $seeds kg")
                        Text("• Water requirement: $water L")
                        Text("• Fertilizer planning required")

                        Spacer(Modifier.height(10.dp))

                        Text("Tips", fontWeight = FontWeight.Bold)

                        Text("• Use high-quality seeds")
                        Text("• Maintain irrigation schedule")
                        Text("• Monitor crop health")
                        Text("• Practice crop rotation")
                    }
                }
            }
        }
    }

    // 🌱 DIALOG
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {},
            text = {
                Column {

                    Text("Select Crop", fontWeight = FontWeight.Bold)

                    Spacer(Modifier.height(10.dp))

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        leadingIcon = { Icon(Icons.Default.Search, null) },
                        placeholder = { Text("Search crop") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(10.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier.height(300.dp)
                    ) {
                        items(filtered) { crop ->
                            Column(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .clickable {
                                        selectedCrop = crop.name
                                        showDialog = false
                                    },
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Image(
                                    painter = painterResource(crop.image),
                                    contentDescription = null,
                                    modifier = Modifier.size(70.dp)
                                )

                                Text(crop.name)
                            }
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    Text(
                        "Cancel",
                        modifier = Modifier
                            .align(Alignment.End)
                            .clickable { showDialog = false },
                        color = Color(0xFF2962FF)
                    )
                }
            }
        )
    }
}