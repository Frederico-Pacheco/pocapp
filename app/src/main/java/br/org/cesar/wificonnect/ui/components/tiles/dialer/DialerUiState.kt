package br.org.cesar.wificonnect.ui.components.tiles.dialer

import android.content.Intent
import android.telecom.Call
import br.org.cesar.wificonnect.domain.repository.ICallRepository.Companion.DEFAULT_STATE

data class DialerUiState(
    val callState: Int = -1,
    val callDirection: Int = Call.Details.DIRECTION_UNKNOWN,
    val isSpeakerOn: Boolean = false,
    val isDefaultDialer: Boolean = false,
    val roleIntent: Intent? = null
) {
    fun getCallStateText(): String {
        val direction = when (callDirection) {
            Call.Details.DIRECTION_INCOMING -> "MT:"
            Call.Details.DIRECTION_OUTGOING -> "MO:"
            else -> ""
        }

        return when (callState) {
            Call.STATE_NEW -> "$direction New."
            Call.STATE_DIALING -> "$direction Dialing..."
            Call.STATE_CONNECTING -> "$direction Connecting..."
            Call.STATE_RINGING -> "$direction Ringing..."
            Call.STATE_ACTIVE -> "$direction Active..."
            Call.STATE_DISCONNECTING -> "$direction Disconnecting..."
            Call.STATE_DISCONNECTED -> "$direction Disconnected."
            else -> "..."
        }
    }

    fun isRinging(): Boolean = callState == Call.STATE_RINGING

    fun isEndCallButtonEnabled(): Boolean {
        val endCallStates = setOf(
            Call.STATE_DIALING,
            Call.STATE_RINGING,
            Call.STATE_ACTIVE
        )

        return callState in endCallStates
    }

    fun isSpeakerButtonEnabled(): Boolean {
        val speakerEnabledStates = setOf(
            Call.STATE_DIALING,
            Call.STATE_ACTIVE
        )

        return callState in speakerEnabledStates
    }

    fun canFinishActivity(): Boolean {
        val endCallStates = setOf(
            DEFAULT_STATE,
            Call.STATE_DISCONNECTED
        )

        return callState in endCallStates
    }
}