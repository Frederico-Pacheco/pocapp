package br.org.cesar.wificonnect.domain.repository

import android.telecom.Call
import kotlinx.coroutines.flow.StateFlow

interface ICallRepository {
    var call: Call?
    val stateFlow: StateFlow<Int>
    val speakerStateFlow: StateFlow<Boolean>
    var direction: Int

    fun acceptCall(isDeviceLocked: Boolean)

    fun disconnectCall(isDeviceLocked: Boolean)

    fun setSpeakerState(isSpeakerOn: Boolean)

    companion object {
        const val IDLE_STATE = -1
        const val DEFAULT_STATE = -2
    }
}