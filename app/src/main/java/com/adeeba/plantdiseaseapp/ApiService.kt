package com.adeeba.plantdiseaseapp

import android.content.Context
import android.os.Handler
import android.os.Looper
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

fun sendChatMessage(
    context: Context,
    message: String,
    language: String,
    onResponse: (String) -> Unit
) {

    val API_KEY = "AIzaSyDxJn0_oncZY4tW1ZQQzStSJ8f2bmV-bLc"

    val url =
        "https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent?key=$API_KEY"

    val client = OkHttpClient()

    val jsonBody = """
        {
          "contents": [
            {
              "parts": [
                {
                  "text": "Reply in $language language. $message"
                }
              ]
            }
          ]
        }
    """.trimIndent()

    val body = jsonBody.toRequestBody("application/json".toMediaType())

    val request = Request.Builder()
        .url(url)
        .post(body)
        .build()

    client.newCall(request).enqueue(object : Callback {

        override fun onFailure(call: Call, e: IOException) {
            Handler(Looper.getMainLooper()).post {
                onResponse("⚠️ Network error")
            }
        }

        override fun onResponse(call: Call, response: Response) {

            val result = response.body?.string()

            try {
                val json = JSONObject(result ?: "{}")

                // ✅ HANDLE API ERROR
                if (json.has("error")) {
                    val errorMsg = json.getJSONObject("error")
                        .optString("message", "API error")

                    Handler(Looper.getMainLooper()).post {
                        onResponse("⚠️ $errorMsg")
                    }
                    return
                }

                // ✅ SAFE PARSING (NO CRASH)
                val candidates = json.optJSONArray("candidates")

                val reply = try {

                    val candidates = json.optJSONArray("candidates")

                    if (candidates != null && candidates.length() > 0) {
                        candidates.getJSONObject(0)
                            .optJSONObject("content")
                            ?.optJSONArray("parts")
                            ?.optJSONObject(0)
                            ?.optString("text", "No reply")
                    } else {
                        "⚠️ No candidates in response: ${json.toString()}"
                    }

                } catch (e: Exception) {
                    "⚠️ Parsing failed: ${json.toString()}"
                }

                Handler(Looper.getMainLooper()).post {
                    onResponse(reply ?: "⚠️ No response from AI")
                }

            } catch (e: Exception) {
                Handler(Looper.getMainLooper()).post {
                    onResponse("⚠️ Invalid response format")
                }
            }
        }
    })
}