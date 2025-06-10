package br.org.cesar.wificonnect.ui.screens.tiles.playstore

sealed class InstallAppUiEvent {
    data object Initialize : InstallAppUiEvent()

    data class RunningStateChanged(
        val isRunning: Boolean
    ) : InstallAppUiEvent()
}