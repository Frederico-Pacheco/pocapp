package br.org.cesar.wificonnect.ui.screens.tiles.playstore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.org.cesar.wificonnect.BuildConfig
import br.org.cesar.wificonnect.domain.usecase.UseCaseListener
import br.org.cesar.wificonnect.domain.usecase.UseCaseStatus
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
            updateState(isRunning = true)
        }

        override fun onUseCaseMsgReceived(msg: String) {
            updateState(listenerMessage = msg)
        }

        override fun onUseCaseSuccess() {
            onUseCaseMsgReceived("App Installation Completed!")

            updateState(
                isRunning = false,
                useCaseStatus = UseCaseStatus.SUCCESS
            )
        }

        override fun onUseCaseFailed(reason: String?) {
            onUseCaseMsgReceived("App Installation Failed: $reason")

            updateState(
                isRunning = false,
                useCaseStatus = UseCaseStatus.ERROR
            )
        }
    }

    init {
        updateAppConfig()
        viewModelScope.launch {
            useCase.state.collect { useCaseState ->
                _uiState.value = _uiState.value.copy(
                    durationMillis = useCaseState.durationMillis,
                    isRunning = if (useCaseState.durationMillis != null) false else uiState.value.isRunning
                )
            }
        }
    }

    fun onUiEvent(event: InstallAppUiEvent) {
        when (event) {
            is InstallAppUiEvent.Initialize -> initUseCase()
            is InstallAppUiEvent.RunningStateChanged -> updateState(isRunning = event.isRunning)
        }
    }

    private fun updateState(
        companyName: String? = null,
        packageName: String? = null,
        listenerMessage: String? = null,
        useCaseStatus: UseCaseStatus? = null,
        isRunning: Boolean? = null,
    ) {
        _uiState.update { currentState ->
            currentState.copy(
                companyName = companyName ?: currentState.companyName,
                packageName = packageName ?: currentState.packageName,
                listenerMessage = listenerMessage ?: currentState.listenerMessage,
                useCaseStatus = useCaseStatus ?: currentState.useCaseStatus,
                isRunning = isRunning ?: currentState.isRunning,
            )
        }
    }

    private fun updateAppConfig() {
        updateState(
            companyName = uiState.value.companyName ?: BuildConfig.APP_COMPANY,
            packageName = uiState.value.packageName ?: BuildConfig.APP_PKG,
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