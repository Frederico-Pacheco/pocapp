package br.org.cesar.wificonnect.domain.usecase.dialer

import android.app.role.RoleManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.telecom.TelecomManager
import android.util.Log
import androidx.core.content.ContextCompat
import br.org.cesar.wificonnect.domain.repository.ICallRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class DialerUseCase @Inject constructor(
    @ApplicationContext private val mContext: Context,
    private val mRepository: ICallRepository,
    val roleManager: RoleManager,
) {
    val callState: StateFlow<Int> by mRepository::stateFlow
    val speakerState: StateFlow<Boolean> by mRepository::speakerStateFlow

    private val mDefaultDialerChangedReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val packageName =
                intent.getStringExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME)
            if (isDialerRoleHeld()) {
                Log.i(TAG, "defaultDialerChanged: setting default to $packageName")
            } else {
                Log.i(TAG, "defaultDialerChanged: restored to $packageName")
            }
        }
    }

    fun registerDialerChangedReceiver() {
        val intentFilter = IntentFilter(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER)

        ContextCompat.registerReceiver(
            mContext,
            mDefaultDialerChangedReceiver,
            intentFilter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    fun unregisterDialerChangedReceiver() {
        mContext.unregisterReceiver(mDefaultDialerChangedReceiver)
    }

    fun isDialerRoleHeld(): Boolean {
        return roleManager.isRoleHeld(RoleManager.ROLE_DIALER)
    }

    fun acceptCall(isDeviceLocked: Boolean) {
        return mRepository.acceptCall(isDeviceLocked)
    }

    fun disconnectCall(isDeviceLocked: Boolean) {
        return mRepository.disconnectCall(isDeviceLocked)
    }

    fun setSpeakerState(isSpeakerOn: Boolean) {
        return mRepository.setSpeakerState(isSpeakerOn)
    }

    fun setCallDirection(direction: Int) {
        mRepository.direction = direction
    }

    companion object {
        private val TAG = DialerUseCase::class.java.simpleName
        const val EXTRA_CALL_NAME = "incoming_call_name"
    }
}