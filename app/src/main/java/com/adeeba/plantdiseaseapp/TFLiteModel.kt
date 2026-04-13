package com.adeeba.plantdiseaseapp

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

data class DiseaseResult(
    val name: String,
    val confidence: Float,
    val description: String,
    val treatment: String,
    val prevention: String
)

class TFLiteModel(context: Context) {

    private var interpreter: Interpreter? = null

    init {
        try {
            val fileDescriptor = context.assets.openFd("model.tflite")
            val inputStream = fileDescriptor.createInputStream()
            val fileChannel = inputStream.channel

            val startOffset = fileDescriptor.startOffset
            val declaredLength = fileDescriptor.declaredLength

            val modelBuffer = fileChannel.map(
                FileChannel.MapMode.READ_ONLY,
                startOffset,
                declaredLength
            )

            interpreter = Interpreter(modelBuffer)

            Log.d("MODEL_STATUS", "Model Loaded Successfully")

        } catch (e: Exception) {
            Log.e("TFLITE_ERROR", "Model load failed: ${e.message}")
        }
    }

    fun predict(bitmap: Bitmap?): DiseaseResult {

        if (bitmap == null) {
            return DiseaseResult(
                "Image Error", 0f,
                "Invalid image", "Retry capture", "Ensure proper lighting"
            )
        }

        if (interpreter == null) {
            return DiseaseResult(
                "Model Not Loaded", 0f,
                "Model failed to load", "Restart app", "Check assets folder"
            )
        }

        return try {

            val input = preprocess(bitmap)

            // ⚠️ KEEP SAME (change only if needed later)
            val output = Array(1) { FloatArray(3) }

            interpreter?.run(input, output)

            Log.d("MODEL_VALUES", output[0].joinToString())

            val maxIndex = output[0].indices.maxByOrNull { output[0][it] } ?: 0
            val confidence = output[0][maxIndex]

            val labels = listOf(
                "Early Blight",
                "Leaf Scorch",
                "Healthy"
            )

            val disease = labels.getOrElse(maxIndex) { "Unknown" }

            // 🔥 LOCAL DATABASE (LIKE API RESPONSE)
            val (description, treatment, prevention) = when (disease) {

                "Early Blight" -> Triple(
                    "Dark brown concentric rings forming target spots. Lower leaves affected first. Yellowing around lesions. Stem lesions may occur.",
                    "Apply Mancozeb 75% WP or Chlorothalonil fungicide at recommended dosage.",
                    "Avoid overhead watering. Remove infected leaves. Maintain proper plant spacing."
                )

                "Leaf Scorch" -> Triple(
                    "Leaves turn brown and dry from edges due to heat stress or lack of water.",
                    "Improve irrigation and ensure adequate watering.",
                    "Avoid extreme heat exposure. Maintain soil moisture."
                )

                "Healthy" -> Triple(
                    "Plant is healthy with no visible disease symptoms.",
                    "No treatment required.",
                    "Maintain proper farming practices and regular monitoring."
                )

                else -> Triple(
                    "No detailed description available.",
                    "Consult agricultural expert.",
                    "Follow proper farming practices."
                )
            }

            DiseaseResult(
                name = disease,
                confidence = confidence,
                description = description,
                treatment = treatment,
                prevention = prevention
            )

        } catch (e: Exception) {
            e.printStackTrace()

            DiseaseResult(
                "Prediction Failed", 0f,
                "Model error occurred",
                "Retry again",
                "Check model configuration"
            )
        }
    }

    private fun preprocess(bitmap: Bitmap): ByteBuffer {

        val resized = Bitmap.createScaledBitmap(bitmap, 224, 224, true)

        val buffer = ByteBuffer.allocateDirect(4 * 224 * 224 * 3)
        buffer.order(ByteOrder.nativeOrder())

        for (y in 0 until 224) {
            for (x in 0 until 224) {

                val pixel = resized.getPixel(x, y)

                buffer.putFloat(((pixel shr 16 and 0xFF) / 255f))
                buffer.putFloat(((pixel shr 8 and 0xFF) / 255f))
                buffer.putFloat(((pixel and 0xFF) / 255f))
            }
        }

        buffer.rewind()
        return buffer
    }
}