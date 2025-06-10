package br.org.cesar.wificonnect.ui.screens.tiles.network.wifirequest

sealed class NetworkRequestUiEvent {

    data object VerifyWifiEnabled : NetworkRequestUiEvent()

    data class UpdatePermissionStatus(val permissionStatus: Int) : NetworkRequestUiEvent()

    data class WiFiRequest(
        val ssid: String?,
        val psk: String?,
    ) : NetworkRequestUiEvent()
}