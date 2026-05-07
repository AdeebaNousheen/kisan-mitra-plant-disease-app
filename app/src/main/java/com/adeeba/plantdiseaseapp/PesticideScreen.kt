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
fun PesticideScreen(onBack: () -> Unit) {

    var selectedCrop by remember { mutableStateOf("Wheat") }
    var showDialog by remember { mutableStateOf(false) }
    var search by remember { mutableStateOf("") }

    var plotSize by remember { mutableStateOf(1.0f) }
    var frequency by remember { mutableStateOf("10–15 days") }

    var showResult by remember { mutableStateOf(false) }

    val crops = listOf(
        Crop("Wheat", R.drawable.wheat),
        Crop("Rice", R.drawable.rice),
        Crop("Banana", R.drawable.banana),
        Crop("Maize", R.drawable.maize),
        Crop("Apple", R.drawable.apple)
    )

    val filtered = crops.filter {
        it.name.contains(search, true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pesticide Guide") },
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

            // 🌱 SELECT CROP
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

            // 🔁 FREQUENCY
            Text("Spray Frequency", fontWeight = FontWeight.Bold)

            Row {
                listOf("7 days", "10–15 days", "20 days").forEach { option ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = frequency == option,
                            onClick = { frequency = option }
                        )
                        Text(option)
                    }
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
                Text("Show Pesticides")
            }

            // 📊 RESULT
            if (showResult) {

                Spacer(Modifier.height(20.dp))

                PesticideResultCard(
                    crop = selectedCrop,
                    plot = plotSize,
                    freq = frequency
                )
            }
        }
    }

    // 🌱 CROP DIALOG
    if (showDialog) {
        CropDialog(
            search = search,
            onSearch = { search = it },
            crops = filtered,
            onSelect = {
                selectedCrop = it
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }
}

//////////////////////////////////////////////////////////
// 🔥 RESULT CARD
//////////////////////////////////////////////////////////

@Composable
fun PesticideResultCard(crop: String, plot: Float, freq: String) {

    val pesticides = getPesticides(crop)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F3F3))
    ) {
        Column(Modifier.padding(16.dp)) {

            Text("Crop: $crop", fontWeight = FontWeight.Bold)

            Spacer(Modifier.height(10.dp))

            Text("Recommended Pesticides", fontWeight = FontWeight.Bold)

            pesticides.forEach { item ->
                Text("• $item")
            }

            Spacer(Modifier.height(10.dp))

            Text("Dosage", fontWeight = FontWeight.Bold)
            Text("~ ${plot * 2} ml per spray (approx)")

            Spacer(Modifier.height(10.dp))

            Text("Frequency: Every $freq")

            Spacer(Modifier.height(10.dp))

            Text("Safety Tips", fontWeight = FontWeight.Bold)
            Text("• Wear gloves\n• Avoid midday spraying\n• Keep away from children")
        }
    }
}

//////////////////////////////////////////////////////////
// 🌱 CROP DIALOG (WITH CANCEL BUTTON)
//////////////////////////////////////////////////////////

@Composable
fun CropDialog(
    search: String,
    onSearch: (String) -> Unit,
    crops: List<Crop>,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        text = {
            Column {

                Text("Select Crop", fontWeight = FontWeight.Bold)

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = search,
                    onValueChange = onSearch,
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    placeholder = { Text("Search crop") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(10.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.height(300.dp)
                ) {
                    items(crops) { crop ->
                        Column(
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable { onSelect(crop.name) },
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
                        .clickable { onDismiss() },
                    color = Color(0xFF2962FF),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    )
}

//////////////////////////////////////////////////////////
// 📦 DATA + LOGIC
//////////////////////////////////////////////////////////


fun getPesticides(crop: String): List<String> {
    return when (crop) {
        "Wheat" -> listOf("Neem Oil", "Chlorpyrifos", "Imidacloprid")
        "Rice" -> listOf("Carbendazim", "Tricyclazole", "Chlorantraniliprole")
        "Banana" -> listOf("Chlorpyrifos", "Imidacloprid", "Neem Oil")
        "Maize" -> listOf("Atrazine", "Spinosad", "Chlorpyrifos")
        "Apple" -> listOf("Captan", "Mancozeb", "Imidacloprid")
        else -> listOf("Neem Oil", "General Spray")
    }
}