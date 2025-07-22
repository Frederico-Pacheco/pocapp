package br.org.cesar.wificonnect.data.service

import android.content.Intent
import android.os.IBinder
import android.telecom.Call
import android.telecom.CallAudioState
import android.telecom.InCallService
import br.org.cesar.wificonnect.domain.repository.ICallRepository
import br.org.cesar.wificonnect.domain.usecase.dialer.DialerUseCase.Companion.EXTRA_CALL_NAME
import br.org.cesar.wificonnect.ui.InCallUiActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DialerInCallService : InCallService() {

    @Inject
    lateinit var callRepository: ICallRepository

    override fun onBind(intent: Intent?): IBinder? {
        observeSpeakerState()
        return super.onBind(intent)
    }

    override fun onCallAdded(call: Call?) {
        val direction = call?.details?.callDirection

        if (direction == callRepository.direction) {
            super.onCallAdded(call)

            val callState = call.details?.state
            if (callState == Call.STATE_RINGING || callState == Call.STATE_CONNECTING) {
                callRepository.call = call

                val intent = Intent(Intent.ACTION_MAIN).apply {
                    setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION or Intent.FLAG_ACTIVITY_NEW_TASK)
                    setClass(applicationContext, InCallUiActivity::class.java)
                    call.details.handle.let {
                        putExtra(EXTRA_CALL_NAME, it.schemeSpecificPart)
                    }
                }

                startActivity(intent)
            }
        }
    }

    override fun onCallRemoved(call: Call) {
        if (callRepository.call == call) {
            callRepository.setSpeakerState(false)
            callRepository.direction = Call.Details.DIRECTION_UNKNOWN
            callRepository.call = null
        }
    }

    private fun observeSpeakerState() {
        CoroutineScope(Dispatchers.Main).launch {
            callRepository.speakerStateFlow.collect { isSpeakerOn ->
                if (isSpeakerOn) {
                    setAudioRoute(CallAudioState.ROUTE_SPEAKER)
                } else {
                    setAudioRoute(CallAudioState.ROUTE_WIRED_OR_EARPIECE)
                }
            }
        }
    }
}