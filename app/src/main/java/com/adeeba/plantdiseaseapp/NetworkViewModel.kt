package com.adeeba.plantdiseaseapp

import android.app.Application
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NetworkViewModel(application: Application) : AndroidViewModel(application) {

    private val connectivityManager =
        application.getSystemService(ConnectivityManager::class.java)

    private val _isConnected = MutableStateFlow(checkConnection())
    val isConnected: StateFlow<Boolean> = _isConnected

    init {
        viewModelScope.launch {
            while (true) {
                _isConnected.value = checkConnection()
                delay(3000) // check every 3 seconds
            }
        }
    }

    private fun checkConnection(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities =
            connectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}