package br.org.cesar.wificonnect.ui.screens.usecaselist

import android.content.ComponentName

data class UseCaseListUiState(
    val isA11yEnabled: Boolean? = null,
    val a11yServiceComponentName: ComponentName? = null,
)