package br.org.cesar.wificonnect.ui.components.tiles.dialer.outgoing

import android.content.Intent

data class DialerOutgoingCallUiState(
    val phoneNumber: String? = null,
    val dialIntent: Intent? = null,
)