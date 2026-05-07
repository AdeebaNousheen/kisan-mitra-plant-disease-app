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
fun FertilizerScreen(onBack: () -> Unit) {

    // ================= STATE =================
    var selectedCrop by remember { mutableStateOf("Wheat") }
    var showCropDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    var plotSize by remember { mutableStateOf(1.0f) }

    var nitrogen by remember { mutableStateOf(50f) }
    var phosphorus by remember { mutableStateOf(25f) }
    var potassium by remember { mutableStateOf(25f) }

    var showEditDialog by remember { mutableStateOf(false) }

    // RESULT
    var showResult by remember { mutableStateOf(false) }
    var totalN by remember { mutableStateOf(0f) }
    var totalP by remember { mutableStateOf(0f) }
    var totalK by remember { mutableStateOf(0f) }

    // ================= CROP LIST =================
    val crops = listOf(
        Crop("Apple", R.drawable.apple),
        Crop("Banana", R.drawable.banana),
        Crop("Wheat", R.drawable.wheat),
        Crop("Rice", R.drawable.rice),
        Crop("Maize", R.drawable.maize),
        Crop("Onion", R.drawable.onion),
        Crop("Almond", R.drawable.almond),
        Crop("Apricot", R.drawable.apricot),
        Crop("Barley", R.drawable.barley),
        Crop("Bean", R.drawable.bean),
        Crop("Grams", R.drawable.grams),
        Crop("Brinjal", R.drawable.brinjal),
        Crop("Coffee", R.drawable.coffee),
        Crop("Cotton", R.drawable.cotton),
        Crop("Peanut", R.drawable.peanut),
        Crop("Pistachio", R.drawable.pistachio),
        Crop("Rose", R.drawable.rose),
        Crop("Watermelon", R.drawable.watermelon)
    )

    val filteredCrops = crops.filter {
        it.name.contains(searchQuery, true)
    }

    val selectedCropObj = crops.find { it.name == selectedCrop }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Fertilizer Calculator") },
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
                .verticalScroll(rememberScrollState())
        ) {

            // ================= CROP SELECT =================
            Text("Select Crop", fontWeight = FontWeight.Bold)

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF6C63FF))
                    .clickable { showCropDialog = true }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                selectedCropObj?.let {
                    Image(
                        painter = painterResource(it.image),
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
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

            // ================= NPK =================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Nutrient quantities", fontWeight = FontWeight.Bold)
                Text(
                    "Edit",
                    color = Color(0xFF2962FF),
                    modifier = Modifier.clickable { showEditDialog = true }
                )
            }

            Spacer(Modifier.height(6.dp))

            Text(
                "Based on crop and field size, nutrients are calculated.",
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                NutrientCard("Nitrogen (N)", nitrogen)
                NutrientCard("Phosphorus (P)", phosphorus)
                NutrientCard("Potassium (K)", potassium)
            }

            Spacer(Modifier.height(20.dp))

            // ================= PLOT =================
            Text("Plot size", fontWeight = FontWeight.Bold)

            Text(
                "Example: 1 acre = 1.0 | Half acre = 0.5",
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall
            )

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

                Box(
                    modifier = Modifier
                        .background(Color(0xFFEDEDED), RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Text("${String.format("%.1f", plotSize)} Acre")
                }

                IconButton(onClick = {
                    plotSize += 0.1f
                }) {
                    Icon(Icons.Default.Add, null)
                }
            }

            Spacer(Modifier.height(20.dp))

            // ================= CALCULATE =================
            Button(
                onClick = {
                    val (n, p, k) = getNPK(selectedCrop)

                    nitrogen = n
                    phosphorus = p
                    potassium = k

                    totalN = n * plotSize
                    totalP = p * plotSize
                    totalK = k * plotSize

                    showResult = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C63FF))
            ) {
                Text("Calculate")
            }

            // ================= RESULT =================
            if (showResult) {

                Spacer(Modifier.height(20.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F3F3))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        Text("Crop: $selectedCrop", fontWeight = FontWeight.Bold)

                        Spacer(Modifier.height(10.dp))

                        Text("Per Acre (Recommended)", fontWeight = FontWeight.SemiBold)
                        Text("N: $nitrogen kg")
                        Text("P: $phosphorus kg")
                        Text("K: $potassium kg")

                        Spacer(Modifier.height(10.dp))

                        Text("Total (Based on ${String.format("%.1f", plotSize)} Acre)", fontWeight = FontWeight.SemiBold)
                        Text("N: ${String.format("%.1f", totalN)} kg")
                        Text("P: ${String.format("%.1f", totalP)} kg")
                        Text("K: ${String.format("%.1f", totalK)} kg")

                        Spacer(Modifier.height(10.dp))

                        Text("Schedule", fontWeight = FontWeight.Bold)
                        Text("Day 1 → ${String.format("%.1f", totalN * 0.3f)} kg")
                        Text("Day 30 → ${String.format("%.1f", totalN * 0.4f)} kg")
                        Text("Day 60 → ${String.format("%.1f", totalN * 0.3f)} kg")
                    }
                }
            }
        }
    }

    // ================= CROP DIALOG =================
    if (showCropDialog) {
        AlertDialog(
            onDismissRequest = { showCropDialog = false },
            confirmButton = {},
            text = {
                Column {

                    Text("Select your crop", fontWeight = FontWeight.Bold)

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
                        modifier = Modifier.height(350.dp)
                    ) {
                        items(filteredCrops) { crop ->
                            Column(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .clickable {
                                        selectedCrop = crop.name
                                        showCropDialog = false
                                    },
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Image(
                                    painter = painterResource(crop.image),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(70.dp)
                                        .clip(CircleShape)
                                )

                                Spacer(Modifier.height(6.dp))
                                Text(crop.name)
                            }
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    Text(
                        "Cancel",
                        modifier = Modifier
                            .align(Alignment.End)
                            .clickable { showCropDialog = false },
                        color = Color.Blue
                    )
                }
            }
        )
    }

    // ================= EDIT DIALOG =================
    if (showEditDialog) {

        var tempN by remember { mutableStateOf(nitrogen.toString()) }
        var tempP by remember { mutableStateOf(phosphorus.toString()) }
        var tempK by remember { mutableStateOf(potassium.toString()) }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            confirmButton = {},
            text = {
                Column {

                    Text("Edit Nutrients", fontWeight = FontWeight.Bold)

                    Spacer(Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        InputBox("N", tempN) { tempN = it }
                        InputBox("P", tempP) { tempP = it }
                        InputBox("K", tempK) { tempK = it }
                    }

                    Spacer(Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Reset", modifier = Modifier.clickable {
                            tempN = "50"
                            tempP = "25"
                            tempK = "25"
                        })

                        Text(
                            "Save",
                            color = Color.Blue,
                            modifier = Modifier.clickable {
                                nitrogen = tempN.toFloatOrNull() ?: 50f
                                phosphorus = tempP.toFloatOrNull() ?: 25f
                                potassium = tempK.toFloatOrNull() ?: 25f
                                showEditDialog = false
                            }
                        )
                    }
                }
            }
        )
    }
}

// ================= HELPERS ================

@Composable
fun NutrientCard(title: String, value: Float) {
    Card(
        modifier = Modifier.width(110.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(10.dp)) {
            Text(title, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text("$value kg")
        }
    }
}

@Composable
fun InputBox(label: String, value: String, onChange: (String) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label)
        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            singleLine = true,
            modifier = Modifier.width(80.dp)
        )
    }
}

fun getNPK(crop: String): Triple<Float, Float, Float> {
    return when (crop) {
        "Wheat" -> Triple(50f, 40f, 30f)
        "Rice" -> Triple(70f, 35f, 30f)
        "Maize" -> Triple(60f, 30f, 20f)
        "Banana" -> Triple(150f, 60f, 150f)
        else -> Triple(50f, 25f, 25f)
    }
}
