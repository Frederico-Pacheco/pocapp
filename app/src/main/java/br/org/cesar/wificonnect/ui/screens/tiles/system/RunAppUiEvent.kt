package br.org.cesar.wificonnect.ui.screens.tiles.system

import android.content.Intent

sealed class RunAppUiEvent {
    data class UpdateAppIntent(val callback: (Intent?) -> Unit) : RunAppUiEvent()
}