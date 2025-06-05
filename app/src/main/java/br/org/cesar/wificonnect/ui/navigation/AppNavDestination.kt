package br.org.cesar.wificonnect.ui.navigation

import kotlinx.serialization.Serializable

sealed interface AppNavDestination {
    @Serializable
    data object Root : AppNavDestination

    @Serializable
    data class NetworkRequest(
        val autoRun: Boolean = false,
        val wifiSsid: String? = null,
        val wifiPsk: String? = null
    ) : AppNavDestination
}