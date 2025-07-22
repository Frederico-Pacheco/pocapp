package br.org.cesar.wificonnect.ui.components.tiles.dialer

import android.app.KeyguardManager
import android.app.role.RoleManager
import android.content.Intent
import android.telecom.Call
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.org.cesar.wificonnect.domain.usecase.dialer.DialerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DialerViewModel @Inject constructor(
    private val mUseCase: DialerUseCase,
    private val mKeyguardManager: KeyguardManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(DialerUiState())
    val uiState: StateFlow<DialerUiState> = _uiState.asStateFlow()

    init {
        updateRoleIntent()
        observeSpeakerState()
        observeCallState()
    }

    fun onUiEvent(event: DialerUiEvent) {
        when (event) {
            is DialerUiEvent.RegisterDialerChangedReceiver -> {
                mUseCase.registerDialerChangedReceiver()
            }

            is DialerUiEvent.UnregisterDialerChangedReceiver -> {
                mUseCase.unregisterDialerChangedReceiver()
            }

            is DialerUiEvent.CheckDefaultDialerStatus -> {
                updateState(isDefaultDialer = mUseCase.isDialerRoleHeld())
            }

            is DialerUiEvent.StartCall -> {
                mUseCase.acceptCall(mKeyguardManager.isKeyguardLocked)
            }

            is DialerUiEvent.EndCall -> {
                mUseCase.disconnectCall(mKeyguardManager.isKeyguardLocked)
            }

            is DialerUiEvent.UpdateCallDirection -> updateCallDirection(event.direction)

            is DialerUiEvent.UpdateSpeakerState -> {
                mUseCase.setSpeakerState(event.isSpeakerOn)
            }
        }
    }

    private fun updateState(
        callState: Int? = null,
        callDirection: Int? = null,
        isSpeakerOn: Boolean? = null,
        isDefaultDialer: Boolean? = null,
        roleIntent: Intent? = null
    ) {
        _uiState.update { currentState ->
            currentState.copy(
                callState = callState ?: currentState.callState,
                callDirection = callDirection ?: currentState.callDirection,
                isSpeakerOn = isSpeakerOn ?: currentState.isSpeakerOn,
                isDefaultDialer = isDefaultDialer ?: currentState.isDefaultDialer,
                roleIntent = roleIntent ?: currentState.roleIntent
            )
        }
    }

    private fun updateRoleIntent() {
        val isRoleHeld = mUseCase.isDialerRoleHeld()
        updateState(isDefaultDialer = isRoleHeld)

        val intent =
            if (mUseCase.roleManager.isRoleAvailable(RoleManager.ROLE_DIALER) && !isRoleHeld) {
                mUseCase.roleManager.createRequestRoleIntent(RoleManager.ROLE_DIALER)
            } else {
                null
            }

        updateState(roleIntent = intent)
    }

    private fun updateCallDirection(direction: Int) {
        mUseCase.setCallDirection(direction)
        updateState(callDirection = direction)

        if (direction == Call.Details.DIRECTION_UNKNOWN) {
            onUiEvent(DialerUiEvent.EndCall)
        }
    }

    private fun observeCallState() {
        viewModelScope.launch {
            mUseCase.callState.collect { callState ->
                updateState(callState = callState)
                if (callState == Call.STATE_DISCONNECTED) {
                    updateCallDirection(Call.Details.DIRECTION_UNKNOWN)
                }
            }
        }
    }

    private fun observeSpeakerState() {
        viewModelScope.launch {
            mUseCase.speakerState.collect { isSpeakerOn ->
                updateState(isSpeakerOn = isSpeakerOn)
            }
        }
    }
}