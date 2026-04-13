package com.adeeba.plantdiseaseapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource

@Composable
fun ToolScreen(toolName: String) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        // ✅ TITLE (dynamic)
        val title = when (toolName) {
            "Fertilizer" -> stringResource(R.string.fertilizer)
            "Pesticide" -> stringResource(R.string.pesticide)
            "Farming" -> stringResource(R.string.farming)
            else -> toolName
        }

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        when (toolName) {
            "Fertilizer" -> FertilizerContent()
            "Pesticide" -> PesticideContent()
            "Farming" -> FarmingContent()
        }
    }
}

@Composable
fun FertilizerContent() {

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(stringResource(R.string.tool_title_fertilizer), fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(10.dp))

            Text(stringResource(R.string.fertilizer_1))
            Text(stringResource(R.string.fertilizer_2))
            Text(stringResource(R.string.fertilizer_3))

            Spacer(modifier = Modifier.height(10.dp))

            Text(stringResource(R.string.fertilizer_tips_title), fontWeight = FontWeight.SemiBold)
            Text(stringResource(R.string.fertilizer_tip1))
            Text(stringResource(R.string.fertilizer_tip2))
            Text(stringResource(R.string.fertilizer_tip3))
        }
    }
}

@Composable
fun PesticideContent() {

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(stringResource(R.string.tool_title_pesticide), fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(10.dp))

            Text(stringResource(R.string.pesticide_1))
            Text(stringResource(R.string.pesticide_2))
            Text(stringResource(R.string.pesticide_3))

            Spacer(modifier = Modifier.height(10.dp))

            Text(stringResource(R.string.pesticide_safety_title), fontWeight = FontWeight.SemiBold)
            Text(stringResource(R.string.pesticide_tip1))
            Text(stringResource(R.string.pesticide_tip2))
            Text(stringResource(R.string.pesticide_tip3))
        }
    }
}

@Composable
fun FarmingContent() {

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(stringResource(R.string.tool_title_farming), fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(10.dp))

            Text(stringResource(R.string.farming_1))
            Text(stringResource(R.string.farming_2))
            Text(stringResource(R.string.farming_3))
            Text(stringResource(R.string.farming_4))
            Text(stringResource(R.string.farming_5))
        }
    }
}