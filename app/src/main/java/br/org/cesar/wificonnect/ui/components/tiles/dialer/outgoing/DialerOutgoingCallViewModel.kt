package br.org.cesar.wificonnect.ui.components.tiles.dialer.outgoing

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import br.org.cesar.wificonnect.BuildConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class DialerOutgoingCallViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(DialerOutgoingCallUiState())
    val uiState: StateFlow<DialerOutgoingCallUiState> = _uiState.asStateFlow()

    init {
        updatePhoneConfig()
    }

    fun onUiEvent(event: DialerOutgoingCallUiEvent) {
        when (event) {
            is DialerOutgoingCallUiEvent.UpdateDialIntent -> updateDialIntent()
        }
    }

    private fun updateState(
        phoneNumber: String? = null,
        dialIntent: Intent? = null,
    ) {
        _uiState.update { currentState ->
            currentState.copy(
                phoneNumber = phoneNumber ?: currentState.phoneNumber,
                dialIntent = dialIntent ?: currentState.dialIntent,
            )
        }
    }

    private fun updateDialIntent() {
        val phoneNumber = uiState.value.phoneNumber ?: ""
        updateState(dialIntent = getDialIntent(phoneNumber))
    }

    private fun getDialIntent(number: String): Intent {
        val dialNumber: Uri = Uri.fromParts("tel", number, null);
        return Intent(Intent.ACTION_DIAL).apply {
            setData(dialNumber)
        }
    }

    private fun updatePhoneConfig() {
        val currentState = uiState.value

        updateState(
            phoneNumber = currentState.phoneNumber ?: BuildConfig.PHONE_NUMBER,
        )
    }
}