package br.org.cesar.wificonnect.ui.screens.network

import android.Manifest
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.org.cesar.wificonnect.BuildConfig
import br.org.cesar.wificonnect.common.dispatcher.DispatcherProvider
import br.org.cesar.wificonnect.domain.usecase.UseCaseListener
import br.org.cesar.wificonnect.domain.usecase.UseCaseStatus
import br.org.cesar.wificonnect.domain.usecase.network.NetworkRequestUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NetworkRequestViewModel @Inject constructor(
    private val mNetworkRequestUseCase: NetworkRequestUseCase,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {
    private val _uiState = MutableStateFlow(NetworkRequestUiState())
    val uiState: StateFlow<NetworkRequestUiState> = _uiState.asStateFlow()

    private val useCaseListener = object : UseCaseListener {
        override fun onUseCaseStarted() {
            updateState(isLoading = true)
        }

        override fun onUseCaseMsgReceived(msg: String) {
            updateState(listenerMessage = msg)
        }

        override fun onUseCaseSuccess() {
            // TODO("Not yet implemented")
        }

        override fun onUseCaseFailed(reason: String?) {
            // TODO("Not yet implemented")
        }
    }


    init {
        updateState(useCaseListener = useCaseListener)
        updateWifiConfig()
        onUiEvent(NetworkRequestUiEvent.VerifyWifiEnabled)
    }

    @Throws(SecurityException::class)
    fun onUiEvent(event: NetworkRequestUiEvent) {
        when (event) {
            is NetworkRequestUiEvent.VerifyWifiEnabled -> updateState(
                isWifiEnabled = mNetworkRequestUseCase.isWifiEnabled()
            )

            is NetworkRequestUiEvent.UpdatePermissionStatus -> updateState(
                permissionStatus = event.permissionStatus
            )

            is NetworkRequestUiEvent.WiFiRequest -> performWifiRequest(
                event.ssid,
                event.psk,
                event.listener
            )
        }
    }

    private fun updateState(
        wifiSsid: String? = null,
        wifiPsk: String? = null,
        isWifiEnabled: Boolean? = null,
        requestDurations: List<Long?>? = null,
        useCaseListener: UseCaseListener? = null,
        listenerMessage: String? = null,
        useCaseStatus: UseCaseStatus? = null,
        permissionStatus: Int? = null,
        isLoading: Boolean? = null
    ) {
        _uiState.update { currentState ->
            currentState.copy(
                wifiSsid = wifiSsid ?: currentState.wifiSsid,
                wifiPsk = wifiPsk ?: currentState.wifiPsk,
                isWifiEnabled = isWifiEnabled ?: currentState.isWifiEnabled,
                requestDurations = requestDurations ?: currentState.requestDurations,
                useCaseListener = useCaseListener ?: currentState.useCaseListener,
                listenerMessage = listenerMessage ?: currentState.listenerMessage,
                useCaseStatus = useCaseStatus ?: currentState.useCaseStatus,
                permissionStatus = permissionStatus ?: currentState.permissionStatus,
                isLoading = isLoading ?: currentState.isLoading
            )
        }
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    private fun performWifiRequest(ssid: String?, psk: String?, listener: UseCaseListener?) {
        updateState(useCaseStatus = UseCaseStatus.NOT_EXECUTED)

        val executionCount = 2
        val requestDurations: MutableList<Long?> = mutableListOf()

        viewModelScope.launch(dispatcherProvider.io) {
            for (index in 1..executionCount) {
                val requestDuration =
                    mNetworkRequestUseCase.measureNetworkRequest(ssid, psk, listener) ?: break

                val delayTime = 10000L
                listener?.onUseCaseMsgReceived("Successfully connected! Waiting ${delayTime/1000} seconds...")
                delay(delayTime)
                mNetworkRequestUseCase.unregisterNetworkCallback(listener)

                requestDurations.add(requestDuration)
            }

            updateState(
                isLoading = false,
                requestDurations = requestDurations,
                useCaseStatus = if (requestDurations.all { it != null}) {
                    UseCaseStatus.SUCCESS
                } else {
                    UseCaseStatus.ERROR
                },
            )
        }
    }

    private fun updateWifiConfig() {
        val currentState = uiState.value

        updateState(
            wifiSsid = currentState.wifiSsid ?: BuildConfig.WIFI_SSID,
            wifiPsk = currentState.wifiPsk ?: BuildConfig.WIFI_PKS
        )
    }
}