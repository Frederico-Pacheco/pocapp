package br.org.cesar.wificonnect.ui.components.tiles.network.mobilesignal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.org.cesar.wificonnect.common.dispatcher.DispatcherProvider
import br.org.cesar.wificonnect.domain.usecase.network.mobilesignal.NetworkSignalUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NetworkSignalViewModel @Inject constructor(
    private val mNetworkSignalUseCase: NetworkSignalUseCase,
    private val mDispatcherProvider: DispatcherProvider
) : ViewModel() {
    private val _uiState = MutableStateFlow(NetworkSignalUiState())
    val uiState: StateFlow<NetworkSignalUiState> = _uiState.asStateFlow()

    fun onUiEvent(event: NetworkSignalUiEvent) {
        when (event) {
            is NetworkSignalUiEvent.UpdatePermissionStatus -> updateState()
            is NetworkSignalUiEvent.MeasureNetworkSignal -> measureNetworkSignal()
        }
    }

    private fun updateState(
        isRunning: Boolean? = null,
        permissionStatus: Int? = null,
    ) {
        _uiState.update { currentState ->
            currentState.copy(
                isRunning = isRunning ?: currentState.isRunning,
                permissionStatus = permissionStatus ?: currentState.permissionStatus,
            )
        }
    }

    private fun measureNetworkSignal() {
        updateState(isRunning = true)
        viewModelScope.launch(mDispatcherProvider.io) {
            mNetworkSignalUseCase.registerSignalStrengthListener()
            delay(15000)
            mNetworkSignalUseCase.unregisterSignalStrengthListener()

            mNetworkSignalUseCase.cleanup()
            updateState(isRunning = false)
        }
    }
}