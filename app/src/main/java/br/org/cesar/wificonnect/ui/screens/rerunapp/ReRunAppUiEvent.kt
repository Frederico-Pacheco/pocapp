package br.org.cesar.wificonnect.ui.screens.rerunapp

import android.content.ComponentName
import android.content.ContentResolver
import android.content.Intent
import br.org.cesar.wificonnect.domain.usecase.UseCaseStatus

sealed class ReRunAppUiEvent {
    data class UpdateA11yComponentName(val componentName: ComponentName) : ReRunAppUiEvent()

    data class CheckA11yState(val contentResolver: ContentResolver) : ReRunAppUiEvent()

    data object UseCaseInitialize : ReRunAppUiEvent()

    data class UseCaseRunningStateChanged(val isRunning: Boolean) : ReRunAppUiEvent()

    data class UseCaseStatusChanged(val useCaseStatus: UseCaseStatus) : ReRunAppUiEvent()

    data class UpdateAppIntent(val callback: (Intent?) -> Unit) : ReRunAppUiEvent()
}