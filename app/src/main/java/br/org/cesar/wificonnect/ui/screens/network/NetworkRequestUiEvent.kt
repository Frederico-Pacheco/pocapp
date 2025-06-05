package br.org.cesar.wificonnect.ui.screens.network

import android.content.ComponentName

sealed class NetworkRequestUiEvent {

    data object VerifyWifiEnabled : NetworkRequestUiEvent()

    data class VerifyAccessibilityServiceEnabled(
        val serviceSetting: String?,
        val expectedComponentName: ComponentName
    ) : NetworkRequestUiEvent()

    data class UpdatePermissionStatus(val permissionStatus: Int) : NetworkRequestUiEvent()

    data class WiFiRequest(
        val ssid: String?,
        val psk: String?,
    ) : NetworkRequestUiEvent()
}