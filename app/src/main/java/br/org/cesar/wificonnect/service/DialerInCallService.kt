package br.org.cesar.wificonnect.service

import android.content.Intent
import android.os.IBinder
import android.telecom.Call
import android.telecom.InCallService
import br.org.cesar.wificonnect.domain.usecase.dialer.DialerUseCase.Companion.EXTRA_CALL_NAME
import br.org.cesar.wificonnect.domain.usecase.dialer.IncomingCall
import br.org.cesar.wificonnect.ui.InCallUiActivity

class DialerInCallService : InCallService() {

    override fun onBind(intent: Intent?): IBinder? {
        return super.onBind(intent)
    }

    override fun onCallAdded(call: Call?) {
        super.onCallAdded(call)

        val callState = call?.details?.state
        if (callState == Call.STATE_RINGING || callState == Call.STATE_CONNECTING) {
            IncomingCall.call = call

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

    override fun onCallRemoved(call: Call) {
        if (IncomingCall.call == call) {
            IncomingCall.call = null
        }
    }
}