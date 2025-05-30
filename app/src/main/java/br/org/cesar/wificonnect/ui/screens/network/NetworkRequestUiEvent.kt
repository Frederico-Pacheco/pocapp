package br.org.cesar.wificonnect.ui.screens.network

import br.org.cesar.wificonnect.domain.usecase.UseCaseListener

sealed class NetworkRequestUiEvent {

    data object VerifyWifiEnabled : NetworkRequestUiEvent()

    data class UpdatePermissionStatus(val permissionStatus: Int) : NetworkRequestUiEvent()

    data class WiFiRequest(
        val ssid: String?,
        val psk: String?,
        val listener: UseCaseListener? = null
    ) : NetworkRequestUiEvent()
}