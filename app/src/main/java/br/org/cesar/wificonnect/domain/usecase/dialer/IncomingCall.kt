package br.org.cesar.wificonnect.domain.usecase.dialer

import android.telecom.Call
import android.telecom.VideoProfile
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object IncomingCall {
    const val IDLE_STATE = -1
    const val DEFAULT_STATE = -2
    private val TAG = IncomingCall::class.simpleName
    private var wasAnswered = false

    private val _callState = MutableStateFlow(IDLE_STATE)
    val callState: StateFlow<Int> = _callState

    var call: Call? = null
        set(newValue) {
            field = newValue
            setIncomingCall()
        }

    private var isAcceptWhenLocked = false
        set(newValue) {
            call?.let { field = newValue }
        }

    private val mCallback = object : Call.Callback() {
        override fun onStateChanged(call: Call, state: Int) {
            _callState.value = state
            when (state) {
                Call.STATE_ACTIVE -> {
                    wasAnswered = true
                }

                else -> {}
            }
        }

        override fun onCallDestroyed(call: Call?) {
            if (!wasAnswered) Log.w(TAG, "❌ Incoming call was missed (not answered)")
            call?.unregisterCallback(this)
        }
    }

    fun acceptCall(isDeviceLocked: Boolean) {
        isAcceptWhenLocked = isDeviceLocked

        call?.takeIf { incomingCall ->
            incomingCall.details.state == Call.STATE_RINGING
        }?.answer(VideoProfile.STATE_AUDIO_ONLY)
    }

    fun disconnectCall(isDeviceLocked: Boolean) {
        isAcceptWhenLocked = isDeviceLocked

        call?.apply {
            disconnect()
            call = null
        }
    }

    private fun setIncomingCall() {
        call?.apply {
            registerCallback(mCallback)
            _callState.value = details?.state ?: DEFAULT_STATE
            isAcceptWhenLocked = false
        }
    }
}