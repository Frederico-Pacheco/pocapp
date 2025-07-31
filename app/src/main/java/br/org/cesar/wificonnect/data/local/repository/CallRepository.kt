package br.org.cesar.wificonnect.data.local.repository

import android.telecom.Call
import android.telecom.VideoProfile
import android.util.Log
import br.org.cesar.wificonnect.domain.repository.ICallRepository
import br.org.cesar.wificonnect.domain.repository.ICallRepository.Companion.DEFAULT_STATE
import br.org.cesar.wificonnect.domain.repository.ICallRepository.Companion.IDLE_STATE
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class CallRepository @Inject constructor() : ICallRepository {
    private val _state = MutableStateFlow(IDLE_STATE)
    override val stateFlow: StateFlow<Int> = _state

    private val _speakerState = MutableStateFlow(false)
    override val speakerStateFlow: StateFlow<Boolean> = _speakerState

    private var wasAnswered = false

    private var isAcceptWhenLocked = false
        set(newValue) {
            call?.let { field = newValue }
        }

    private val mCallback = object : Call.Callback() {
        override fun onStateChanged(call: Call, state: Int) {
            _state.value = state
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

    override var call: Call? = null
        set(newValue) {
            field = newValue
            setIncomingCall()
        }

    override var direction: Int = Call.Details.DIRECTION_UNKNOWN

    override fun acceptCall(isDeviceLocked: Boolean) {
        isAcceptWhenLocked = isDeviceLocked

        call?.takeIf { incomingCall ->
            incomingCall.details.state == Call.STATE_RINGING
        }?.answer(VideoProfile.STATE_AUDIO_ONLY)
    }

    override fun disconnectCall(isDeviceLocked: Boolean) {
        isAcceptWhenLocked = isDeviceLocked

        call?.apply {
            disconnect()
            setSpeakerState(false)
            direction = Call.Details.DIRECTION_UNKNOWN
            call = null
        }
    }

    override fun setSpeakerState(isSpeakerOn: Boolean) {
        _speakerState.value = isSpeakerOn
    }

    private fun setIncomingCall() {
        call?.apply {
            registerCallback(mCallback)
            _state.value = details?.state ?: DEFAULT_STATE
            isAcceptWhenLocked = false
        }
    }

    companion object {
        private val TAG = CallRepository::class.simpleName
    }
}