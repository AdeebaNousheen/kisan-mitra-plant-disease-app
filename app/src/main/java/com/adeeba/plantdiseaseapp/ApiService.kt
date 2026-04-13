package com.adeeba.plantdiseaseapp

import android.content.Context
import android.provider.Settings
import android.os.Handler
import android.os.Looper
import android.util.Log
import okhttp3.*
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

// =========================
// 🌱 1. DISEASE DETECTION
// =========================
fun sendToApi(
    context: Context,
    file: File,
    language: String,
    onResult: (String, Double, String, String, String) -> Unit,
    onDone: () -> Unit
) {

    val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    val deviceId = Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.ANDROID_ID
    ) ?: "unknown_device"

    val requestBody = MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("device_id", deviceId)
        .addFormDataPart("language", language.ifEmpty { "english" })
        .addFormDataPart(
            "image",
            file.name,
            file.asRequestBody("image/*".toMediaTypeOrNull())
        )
        .build()

    val request = Request.Builder()
        .url("https://kisan-mitra-production.up.railway.app/api/v1/detect/upload")
        .post(requestBody)
        .build()

    client.newCall(request).enqueue(object : Callback {

        override fun onFailure(call: Call, e: IOException) {
            Log.e("API_ERROR", "Failure: ${e.message}")
            Handler(Looper.getMainLooper()).post {
                onResult("Network Error", 0.0, "Check internet", "Retry", "")
                onDone()
            }
        }

        override fun onResponse(call: Call, response: Response) {

            val body = response.body?.string()
            Log.d("API_RESPONSE", body ?: "EMPTY")

            if (!response.isSuccessful) {
                Handler(Looper.getMainLooper()).post {
                    onResult("Server Error", 0.0, "", "", "")
                    onDone()
                }
                return
            }

            try {
                val json = JSONObject(body ?: "{}")

                if (json.optString("status") != "success") {
                    throw Exception("API Error")
                }

                val data = json.optJSONObject("data") ?: JSONObject()

                val diseaseObj = data.optJSONObject("disease") ?: JSONObject()
                val pesticideObj = data.optJSONObject("pesticide_recommendation") ?: JSONObject()

                val name = diseaseObj.optString("name", "Unknown")
                val confidence = diseaseObj.optDouble("confidence_score", 0.0)
                val description = diseaseObj.optString("symptoms", "")

                val treatment = pesticideObj.optString("primary_pesticide", "")
                val prevention = pesticideObj.optString("safety", "")

                Handler(Looper.getMainLooper()).post {
                    onResult(name, confidence, description, treatment, prevention)
                    onDone()
                }

            } catch (e: Exception) {
                Log.e("API_PARSE", "Error: ${e.message}")

                Handler(Looper.getMainLooper()).post {
                    onResult("Parsing Error", 0.0, "", "", "")
                    onDone()
                }
            }
        }
    })
}


// =========================
// 🤖 CHATBOT (UPGRADED)
// =========================
fun sendChatMessage(
    context: Context,
    message: String,
    language: String,
    onResponse: (String) -> Unit
) {

    val client = OkHttpClient()

    val deviceId = Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.ANDROID_ID
    ) ?: "unknown_device"

    // 🔥 STRONG MULTI-LANGUAGE PROMPT (UPGRADED)
    val finalMessage = """
You are an AI farming assistant.

IMPORTANT:
- Reply ONLY in the SAME language as the user message
- If user types Hindi → reply in Hindi
- If user types Telugu → reply in Telugu
- If user types English → reply in English
- Do NOT force English
- Do NOT say "I will respond in English"

User message:
$message
""".trimIndent()
    val json = JSONObject().apply {
        put("device_id", deviceId)
        put("message", finalMessage)
        put("language", language)
    }

    val body = json.toString()
        .toRequestBody("application/json".toMediaTypeOrNull())

    val request = Request.Builder()
        .url("https://kisan-mitra-production.up.railway.app/api/v1/chatbot/query")
        .post(body)
        .build()

    client.newCall(request).enqueue(object : Callback {

        override fun onFailure(call: Call, e: IOException) {
            Handler(Looper.getMainLooper()).post {
                onResponse("Network error")
            }
        }

        override fun onResponse(call: Call, response: Response) {

            val result = response.body?.string()

            try {
                val json = JSONObject(result ?: "{}")
                val data = json.optJSONObject("data") ?: JSONObject()
                var reply = data.optString("bot_response", "No response")

                // 🔥 MULTI-LANGUAGE VALIDATION
                reply = when (language.lowercase()) {
                    "telugu" -> if (isTelugu(reply)) reply else "⚠️ Telugu not supported:\n\n$reply"
                    "hindi" -> if (isHindi(reply)) reply else "⚠️ हिंदी उपलब्ध नहीं:\n\n$reply"
                    else -> reply
                }

                Handler(Looper.getMainLooper()).post {
                    onResponse(reply)
                }

            } catch (e: Exception) {
                Handler(Looper.getMainLooper()).post {
                    onResponse("Error parsing response")
                }
            }
        }
    })
}


// =========================
// 🌍 LANGUAGE CHECKERS
// =========================
fun isTelugu(text: String): Boolean {
    return text.any { it in '\u0C00'..'\u0C7F' }
}

fun isHindi(text: String): Boolean {
    return text.any { it in '\u0900'..'\u097F' }
}