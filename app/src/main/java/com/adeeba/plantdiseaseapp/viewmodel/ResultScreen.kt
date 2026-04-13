package com.adeeba.plantdiseaseapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

@Composable
fun ResultScreen(
    imagePath: String,
    plant: String,
    disease: String,
    confidence: Float,
    onSave: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = rememberAsyncImagePainter(imagePath),
            contentDescription = "Leaf",
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text("Plant: $plant")
        Text("Disease: $disease")
        Text("Confidence: ${(confidence * 100).toInt()}%")

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = onSave) {
            Text("Save Result")
        }
    }
}