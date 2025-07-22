package br.org.cesar.wificonnect.ui.components.tiles.dialer

import android.app.KeyguardManager
import android.app.role.RoleManager
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.org.cesar.wificonnect.domain.usecase.dialer.DialerUseCase
import br.org.cesar.wificonnect.domain.usecase.dialer.IncomingCall
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

        viewModelScope.launch {
            IncomingCall.callState.collect { callState ->
                updateState(callState = callState)
            }
        }
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
                IncomingCall.acceptCall(mKeyguardManager.isKeyguardLocked)
            }

            is DialerUiEvent.EndCall -> {
                IncomingCall.disconnectCall(mKeyguardManager.isKeyguardLocked)
            }
        }
    }

    private fun updateState(
        callState: Int? = null,
        isDefaultDialer: Boolean? = null,
        roleIntent: Intent? = null
    ) {
        _uiState.update { currentState ->
            currentState.copy(
                callState = callState ?: currentState.callState,
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
}