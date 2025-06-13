package br.org.cesar.wificonnect.ui.screens.rerunapp

import android.content.ComponentName
import android.content.ContentResolver
import android.content.Intent

sealed class ReRunAppUiEvent {
    data class UpdateA11yComponentName(val componentName: ComponentName) : ReRunAppUiEvent()

    data class CheckA11yState(val contentResolver: ContentResolver) : ReRunAppUiEvent()

    data object UseCaseInitialize : ReRunAppUiEvent()

    data class AppRunningStateChanged(
        val isRunning: Boolean
    ) : ReRunAppUiEvent()

    data class UpdateAppIntent(val callback: (Intent?) -> Unit) : ReRunAppUiEvent()
}