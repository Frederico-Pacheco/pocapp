package br.org.cesar.wificonnect.ui.screens.tiles.playstore

import android.content.ComponentName
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.org.cesar.wificonnect.domain.usecase.UseCaseListener
import br.org.cesar.wificonnect.domain.usecase.accessibility.AccessibilityServiceUseCase
import br.org.cesar.wificonnect.domain.usecase.playstore.InstallAppUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InstallAppViewModel @Inject constructor(
    private val useCase: InstallAppUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(InstallAppUiState())
    val uiState: StateFlow<InstallAppUiState> = _uiState.asStateFlow()

    private val mUseCaseListener = object : UseCaseListener {
        override fun onUseCaseStarted() {
            // TODO("Not yet implemented")
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
        viewModelScope.launch {
            useCase.state.collect { useCaseState ->
                _uiState.value = _uiState.value.copy(
                    durationMillis = useCaseState.durationMillis,
                    useCaseStatus = useCaseState.useCaseStatus,
                    isRunning = if (useCaseState.durationMillis != null) false else uiState.value.isRunning
                )
            }
        }
    }

    fun onUiEvent(event: InstallAppUiEvent) {
        when (event) {
            is InstallAppUiEvent.Initialize -> initUseCase()
            is InstallAppUiEvent.RunningStateChanged -> {
                updateState(isRunning = event.isRunning)
            }

            is InstallAppUiEvent.UpdateAccessibilitySettingsAccess -> {
                updateState(canOpenAccessibilitySettings = event.canOpen)
            }

            is InstallAppUiEvent.VerifyAccessibilityServiceEnabled -> {
                isAccessibilityServiceEnabled(event.serviceSetting, event.expectedComponentName)
            }
        }
    }

    private fun updateState(
        packageName: String? = null,
        isAccessibilityServiceEnabled: Boolean? = null,
        canOpenAccessibilitySettings: Boolean? = null,
        listenerMessage: String? = null,
        isRunning: Boolean? = null,
    ) {
        _uiState.update { currentState ->
            currentState.copy(
                packageName = packageName ?: currentState.packageName,
                isAccessibilityServiceEnabled = isAccessibilityServiceEnabled
                    ?: currentState.isAccessibilityServiceEnabled,
                canOpenAccessibilitySettings = canOpenAccessibilitySettings
                    ?: currentState.canOpenAccessibilitySettings,
                listenerMessage = listenerMessage ?: currentState.listenerMessage,
                isRunning = isRunning ?: currentState.isRunning,
            )
        }
    }

    private fun isAccessibilityServiceEnabled(
        serviceSetting: String?,
        expectedComponentName: ComponentName
    ) {
        updateState(
            isAccessibilityServiceEnabled = AccessibilityServiceUseCase.isAccessibilityServiceEnabled(
                serviceSetting,
                expectedComponentName
            )
        )
    }

    private fun initUseCase() {
        val currentState = uiState.value

        useCase.apply {
            companyName = currentState.companyName
            pkgName = currentState.packageName
            useCaseListener = mUseCaseListener
            timeoutMillis = currentState.timeoutMillis
        }
    }
}