package com.adeeba.plantdiseaseapp

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.Alignment

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

    var selectedCrop by remember { mutableStateOf(crops.first()) }

    var temperature by remember { mutableStateOf("Loading...") }
    var weather by remember { mutableStateOf("") }

    var humidity by remember { mutableStateOf("42%") }
    var wind by remember { mutableStateOf("12 km/h") }
    var pressure by remember { mutableStateOf("1012 hPa") }
    var uv by remember { mutableStateOf("Moderate") }

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

                humidity = current.getString("humidity") + "%"
                wind = current.getString("windspeedKmph") + " km/h"
                pressure = current.getString("pressure") + " hPa"
                uv = "Moderate"
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
                .padding(horizontal = 16.dp)
        ) {

            Spacer(Modifier.height(6.dp))

            // HEADER
            Column {

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column {   // 👈 THIS is the key fix

                        Text(
                            "Kisan Mitra",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Smart Farming, strong future 🌿",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Button(
                        onClick = onAssistantClick,
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C63FF))
                    ) {
                        Text("AI Assistant ✨")
                    }
                }
            }
            Spacer(Modifier.height(10.dp))

            // CROPS
            Box(Modifier.fillMaxWidth()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(end = 60.dp)
                ) {
                    items(crops) { crop ->
                        val isSelected = crop == selectedCrop

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { selectedCrop = crop }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF5F5F5))
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) Color(0xFF4CAF50) else Color.LightGray,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(getCropEmoji(crop), fontSize = 26.sp)
                            }

                            Spacer(Modifier.height(4.dp))

                            Text(
                                crop,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                FloatingActionButton(
                    onClick = onAddCropClick,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .offset(x = 6.dp)
                        .size(48.dp),
                    containerColor = Color(0xFF7B83EB),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Add, null, tint = Color.White)
                }
            }

            Spacer(Modifier.height(14.dp))

            // WEATHER CARD
            Card(
                shape = RoundedCornerShape(22.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
            ) {
                Box {

                    Image(
                        painter = painterResource(R.drawable.farm_bg),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color.Black.copy(0.6f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {

                        Column {
                            Text("📍 Hyderabad", color = Color.White)
                            Text("Today • 15 May", color = Color.White.copy(0.7f))

                            Spacer(Modifier.height(6.dp))

                            Text(
                                temperature,
                                fontSize = 42.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("☀️")
                                Spacer(Modifier.width(6.dp))
                                Text(weather, color = Color.White)
                            }
                        }

                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            WeatherItem("💧", "Humidity", humidity)
                            WeatherItem("🌬", "Wind", wind)
                            WeatherItem("⏱", "Pressure", pressure)
                            WeatherItem("☀️", "UV Index", uv)
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // LEAF CARD (UNCHANGED)
            Card(
                shape = RoundedCornerShape(22.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(165.dp),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Box {
                    Image(
                        painter = painterResource(R.drawable.leaf),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(0.55f)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF2E7D32), Color(0xFF66BB6A))
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {

                        Column {
                            Text("Identify crop diseases", color = Color.White, fontWeight = FontWeight.Bold)
                            Text("Detect early, protect your yield", color = Color.White.copy(0.85f))
                        }

                        Button(
                            onClick = { onScanClick(selectedCrop) },
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                        ) {
                            Icon(Icons.Default.CameraAlt, null, tint = Color.Black)
                            Spacer(Modifier.width(6.dp))
                            Text("Capture Now", color = Color.Black)
                        }
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // TOOLS
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Tools", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("View All", color = Color(0xFF6C63FF), modifier = Modifier.clickable {
                    onToolClick("view_all")
                })
            }

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ToolCard("Fertilizer", R.drawable.fertilizer, Color(0xFFE3F2FD)) {
                    onToolClick("fertilizer")
                }
                ToolCard("Pesticide", R.drawable.pesticide, Color(0xFFFFEBEE)) {
                    onToolClick("pesticide")
                }
                ToolCard("Farming", R.drawable.farming, Color(0xFFE8F5E9)) {
                    onToolClick("farming")
                }
            }

            Spacer(Modifier.height(16.dp))

            Text("Quick Tips", fontWeight = FontWeight.Bold)

            TipCard("Water crops early morning", Icons.Default.WaterDrop)
            TipCard("Avoid overuse of pesticides", Icons.Default.Warning)
            TipCard("Maintain soil quality", Icons.Default.Grass)

            Spacer(Modifier.height(100.dp))
        }

        // FAB (FIXED ALIGN ERROR)
        FloatingActionButton(
            onClick = { onScanClick(selectedCrop) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = Color(0xFF4CAF50)
        ) {
            Icon(Icons.Default.CameraAlt, null)
        }
    }
}
@Composable
fun WeatherItem(icon: String, title: String, value: String) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(65.dp) // 👈 keeps spacing fixed
    ) {

        Text(icon, fontSize = 14.sp) // 👈 SMALL icon

        Spacer(Modifier.height(2.dp))

        Text(
            text = title,
            color = Color.White.copy(0.7f),
            fontSize = 9.sp // 👈 SMALL label
        )

        Text(
            text = value,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ToolCard(
    title: String,
    imageRes: Int,
    bgColor: Color,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .width(120.dp)
            .height(150.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // ✅ IMAGE FIX (NO BLACK ISSUE)
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(bgColor),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp) // 👈 your actual image
                )
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )

            Text(
                text = "Calculator",
                fontSize = 11.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun TipCard(text: String, icon: ImageVector) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = Color(0xFF5C6BC0))
            Spacer(Modifier.width(10.dp))
            Text(text)
        }
    }
}