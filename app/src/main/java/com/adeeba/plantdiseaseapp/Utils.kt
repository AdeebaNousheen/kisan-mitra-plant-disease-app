package com.adeeba.plantdiseaseapp

import android.content.Context
import android.net.*

fun saveLanguage(context: Context, language: String) {
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    prefs.edit().putString("language", language).apply()
}

fun getLanguage(context: Context): String {
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    return prefs.getString("language", null) ?: ""
}
fun isInternetAvailable(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE)
            as ConnectivityManager
    val network = cm.activeNetwork ?: return false
    val caps = cm.getNetworkCapabilities(network) ?: return false

    return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}