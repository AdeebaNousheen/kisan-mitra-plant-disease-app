package com.adeeba.plantdiseaseapp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
@Composable
fun SelectCropsScreen(
    onSave: (List<String>) -> Unit
) {

    val context = LocalContext.current

    var selectedCrops by remember {
        mutableStateOf(CropStorage.getCrops(context))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text("Select crops", fontSize = 22.sp)

        Spacer(modifier = Modifier.height(8.dp))

        Text("Select up to 10 crops (${selectedCrops.size}/10)")

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            items(allCrops) { crop ->

                val isSelected = selectedCrops.contains(crop)

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable {

                        selectedCrops =
                            if (isSelected) {
                                selectedCrops - crop
                            } else if (selectedCrops.size < 10) {
                                selectedCrops + crop
                            } else selectedCrops
                    }
                ) {

                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                if (isSelected) Color(0xFFE7F0FF) else Color.White,
                                CircleShape
                            )
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) Color(0xFF6C5CE7) else Color.LightGray,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(getCropEmoji(crop), fontSize = 26.sp)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(crop, fontSize = 12.sp)
                }
            }
        }

        Button(
            onClick = {
                CropStorage.saveCrops(context, selectedCrops) // 🔥 SAVE HERE
                onSave(selectedCrops)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            enabled = selectedCrops.isNotEmpty(),
            shape = RoundedCornerShape(50)
        ) {
            Text("Save (${selectedCrops.size}/10)")
        }
    }
}