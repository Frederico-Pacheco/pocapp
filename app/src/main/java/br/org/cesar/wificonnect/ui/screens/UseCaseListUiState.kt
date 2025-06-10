package br.org.cesar.wificonnect.ui.screens

import android.content.ComponentName

data class UseCaseListUiState(
    val isA11yEnabled: Boolean? = null,
    val a11yServiceComponentName: ComponentName? = null,
)