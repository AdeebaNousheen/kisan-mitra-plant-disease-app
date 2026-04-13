package com.adeeba.plantdiseaseapp

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

@Composable
fun CropsScreen(
    selectedCrops: List<String>,
    onScanClick: (String) -> Unit,
    onAddCropClick: () -> Unit,
    onAssistantClick: () -> Unit,
    language: String,
    onToolClick: (String) -> Unit,
) {

    val crops = if (selectedCrops.isEmpty()) {
        listOf("Mango", "Onion", "Wheat", "Tomato")
    } else selectedCrops

    var selectedCrop by remember { mutableStateOf(crops.firstOrNull() ?: "Mango") }

    // 🌤 WEATHER STATE
    var temperature by remember { mutableStateOf("Loading...") }
    var weather by remember { mutableStateOf("") }

    // 🌍 WEATHER API
    LaunchedEffect(Unit) {
        try {
            val client = OkHttpClient()

            val request = Request.Builder()
                .url("https://wttr.in/?format=j1")
                .build()

            val response = withContext(Dispatchers.IO) {
                client.newCall(request).execute()
            }

            val json = response.body?.string()

            if (!json.isNullOrEmpty()) {
                val obj = JSONObject(json)
                val current = obj.getJSONArray("current_condition").getJSONObject(0)

                temperature = current.getString("temp_C") + "°C"
                weather = current.getJSONArray("weatherDesc")
                    .getJSONObject(0)
                    .getString("value")
            }

        } catch (e: Exception) {
            temperature = "30°C"
            weather = "Sunny"
        }
    }

    Box(Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            // 🔝 HEADER
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Kisan Mitra",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )

                Button(onClick = onAssistantClick) {
                    Text("AI Assistant ✨")
                }
            }

            Spacer(Modifier.height(16.dp))

            // 🌾 CROPS + FLOAT ➕ BUTTON
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(end = 60.dp) // space for +
                ) {

                    items(crops) { crop ->

                        val isSelected = crop == selectedCrop

                        val borderColor = when (crop) {
                            "Mango" -> Color(0xFFFFA726)
                            "Onion" -> Color(0xFF8D6E63)
                            "Wheat" -> Color(0xFFFFD54F)
                            "Tomato" -> Color(0xFFE57373)
                            else -> Color.Gray
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable {
                                selectedCrop = crop
                            }
                        ) {

                            Box(
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF5F5F5))
                                    .border(
                                        width = if (isSelected) 3.dp else 1.dp,
                                        color = borderColor,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(getCropEmoji(crop), fontSize = 28.sp)
                            }

                            Spacer(Modifier.height(4.dp))

                            Text(
                                crop,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                // ➕ FLOAT BUTTON (FINAL PERFECT VERSION)
                FloatingActionButton(
                    onClick = onAddCropClick,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .offset(x = 10.dp),
                    containerColor = Color(0xFF7B83EB),
                    shape = CircleShape,
                    elevation = FloatingActionButtonDefaults.elevation(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                }
            }

            Spacer(Modifier.height(20.dp))

            // 🌤 WEATHER CARD (WITH BLUR)
            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {

                Row {

                    // LEFT PANEL
                    Box(
                        modifier = Modifier
                            .weight(0.4f)
                            .fillMaxHeight()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFFB2F2BB), Color(0xFF69DB7C))
                                )
                            )
                            .padding(12.dp)
                    ) {

                        Column(
                            verticalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxHeight()
                        ) {

                            Icon(Icons.Default.LocalFlorist, null, tint = Color.White)

                            Text(
                                "Identify crop diseases",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )

                            Button(onClick = { onScanClick(selectedCrop) }) {
                                Text("Capture")
                            }
                        }
                    }

                    // RIGHT PANEL (IMAGE + BLUR)
                    Box(modifier = Modifier.weight(0.6f)) {

                        Image(
                            painter = painterResource(id = R.drawable.farm_bg),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // 🌫 BLUR EFFECT OVERLAY
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.35f))
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("Hyderabad", color = Color.White)
                            Text("Today", color = Color.White)

                            Text(
                                temperature,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            Text(weather, color = Color.White)
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // 🧰 TOOLS
            Text("Tools", fontWeight = FontWeight.Bold)

            Spacer(Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ToolCard("🌱", "Fertilizer")
                ToolCard("💉", "Pesticide")
                ToolCard("🚜", "Farming")
            }

            Spacer(Modifier.height(20.dp))

            // 🌱 QUICK TIPS
            Text("Quick Tips", fontWeight = FontWeight.Bold)

            Spacer(Modifier.height(8.dp))

            Text("• Water crops early morning")
            Text("• Avoid overuse of pesticides")
            Text("• Maintain soil quality")

            Spacer(Modifier.height(100.dp))
        }

        // 📷 CAMERA FLOAT BUTTON
        FloatingActionButton(
            onClick = { onScanClick(selectedCrop) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = Color(0xFF4CAF50)
        ) {
            Icon(Icons.Default.CameraAlt, contentDescription = null)
        }
    }
}

// 🔹 TOOL CARD
@Composable
fun ToolCard(icon: String, title: String) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.size(110.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(icon, fontSize = 28.sp)
            Spacer(Modifier.height(6.dp))
            Text(title)
        }
    }
}