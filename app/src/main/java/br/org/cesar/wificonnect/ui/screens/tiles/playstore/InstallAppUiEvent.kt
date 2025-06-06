package br.org.cesar.wificonnect.ui.screens.tiles.playstore

import android.content.ComponentName

sealed class InstallAppUiEvent {
    data object Initialize : InstallAppUiEvent()

    data class RunningStateChanged(
        val isRunning: Boolean
    ) : InstallAppUiEvent()

    data class UpdateAccessibilitySettingsAccess(
        val canOpen: Boolean
    ) : InstallAppUiEvent()

    data class VerifyAccessibilityServiceEnabled(
        val serviceSetting: String?,
        val expectedComponentName: ComponentName
    ) : InstallAppUiEvent()
}