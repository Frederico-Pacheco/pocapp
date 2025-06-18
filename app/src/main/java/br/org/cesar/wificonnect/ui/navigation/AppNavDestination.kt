package br.org.cesar.wificonnect.ui.navigation

import kotlinx.serialization.Serializable

sealed interface AppNavDestination {
    @Serializable
    data object Root : AppNavDestination

    @Serializable
    data object Main : AppNavDestination

    @Serializable
    data object UseCaseList : AppNavDestination

    @Serializable
    data object ReRunApp : AppNavDestination

    @Serializable
    data class NetworkRequest(
        var autoRun: Boolean = false,
        val wifiSsid: String? = null,
        val wifiPsk: String? = null
    ) : AppNavDestination

    @Serializable
    data class PlayStoreInstall(
        var autoRun: Boolean = false,
        val appPkg: String? = null,
        val appCompany: String? = null
    ) : AppNavDestination
}