package br.org.cesar.wificonnect.ui.screens.rerunapp

import android.content.ComponentName
import br.org.cesar.wificonnect.domain.usecase.UseCaseStatus

data class ReRunAppUiState(
    val companyName: String? = null,
    val packageName: String? = null,
    val listenerMessage: String = "",
    val useCaseStatus: UseCaseStatus = UseCaseStatus.NOT_EXECUTED,
    val isRunning: Boolean = false,
    val timeoutMillis: Long = 10000L,
    val isA11yEnabled: Boolean? = null,
    val a11yServiceComponentName: ComponentName? = null,
)