package br.org.cesar.wificonnect.ui.components.tiles.dialer

import android.content.Intent
import android.telecom.Call

data class DialerUiState(
    val callState: Int = -1,
    val isDefaultDialer: Boolean = false,
    val roleIntent: Intent? = null
) {
    fun getCallStateText(): String {
        return when (callState) {
            Call.STATE_DIALING -> "Dialing..."
            Call.STATE_CONNECTING -> "Connecting..."
            Call.STATE_ACTIVE -> "Active..."
            Call.STATE_DISCONNECTING -> "Disconnecting..."
            Call.STATE_DISCONNECTED -> "Disconnected."
            else -> "..."
        }
    }
}