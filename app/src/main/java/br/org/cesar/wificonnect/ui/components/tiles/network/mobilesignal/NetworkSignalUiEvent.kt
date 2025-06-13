package br.org.cesar.wificonnect.ui.components.tiles.network.mobilesignal

sealed class NetworkSignalUiEvent {
    data class UpdatePermissionStatus(val permissionStatus: Int) : NetworkSignalUiEvent()

    data object MeasureNetworkSignal : NetworkSignalUiEvent()
}