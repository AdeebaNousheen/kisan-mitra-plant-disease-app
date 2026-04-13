package com.adeeba.plantdiseaseapp

import androidx.compose.ui.graphics.Color
import com.adeeba.plantdiseaseapp.ui.theme.SoftPurpleBlue


val allCrops = listOf(
    "Mango","Onion","Wheat","Tomato","Potato","Rice","Cotton","Chilli",
    "Apple","Banana","Carrot","Cauliflower","Cabbage","Brinjal","Garlic",
    "Ginger","Sugarcane","Maize","Soybean","Peas","Beans","Spinach",
    "Pumpkin","Cucumber","Radish","Beetroot","Okra","Lettuce","Papaya",
    "Pomegranate","Guava","Pineapple","Coconut","Coffee","Tea",
    "Turmeric","Mustard","Groundnut","Barley","Millet","Sunflower",
    "Bajra","Jowar","Blackgram","Greengram","Horsegram","Chickpea"
)

fun getCropEmoji(crop: String): String {
    return when (crop) {

        // Fruits
        "Mango" -> "🥭"
        "Apple" -> "🍎"
        "Banana" -> "🍌"
        "Papaya" -> "🍈"
        "Pomegranate" -> "🍎"
        "Guava" -> "🍏"
        "Pineapple" -> "🍍"
        "Coconut" -> "🥥"

        // Vegetables
        "Tomato" -> "🍅"
        "Potato" -> "🥔"
        "Carrot" -> "🥕"
        "Cabbage" -> "🥬"
        "Brinjal" -> "🍆"
        "Chilli" -> "🌶️"
        "Pumpkin" -> "🎃"
        "Cucumber" -> "🥒"
        "Radish" -> "🥕"
        "Beetroot" -> "🥕"
        "Okra" -> "🌿"
        "Lettuce" -> "🥬"
        "Onion" -> "🧅"
        "Garlic" -> "🧄"
        "Ginger" -> "🌿"   // ✅ replaced

        // Crops
        "Wheat" -> "🌾"
        "Rice" -> "🌾"
        "Maize" -> "🌽"
        "Barley" -> "🌾"
        "Millet" -> "🌾"
        "Bajra" -> "🌾"
        "Jowar" -> "🌾"

        // Pulses (FIXED)
        "Blackgram" -> "🌰"
        "Greengram" -> "🌰"
        "Horsegram" -> "🌰"
        "Chickpea" -> "🌰"
        "Beans" -> "🌰"
        "Peas" -> "🌰"

        // Others
        "Coffee" -> "☕"
        "Tea" -> "🍵"
        "Cotton" -> "🧶"
        "Sugarcane" -> "🎋"
        "Soybean" -> "🌰"
        "Spinach" -> "🥬"
        "Sunflower" -> "🌻"
        "Mustard" -> "🌼"
        "Groundnut" -> "🥜"
        "Turmeric" -> "🟡"

        else -> "🌱"
    }
}

fun getCropColor(crop: String): Color {
    return when (crop) {
        "Mango" -> Color(0xFFFFA726)
        "Onion" -> Color(0xFF8D6E63)
        "Wheat" -> Color(0xFFFFD54F)
        "Tomato" -> Color(0xFFE53935)
        "Potato" -> Color(0xFFA1887F)
        "Rice" -> Color(0xFF9CCC65)
        "Cotton" -> Color(0xFFBDBDBD)
        "Chilli" -> Color(0xFFD32F2F)
        else -> SoftPurpleBlue
    }
}