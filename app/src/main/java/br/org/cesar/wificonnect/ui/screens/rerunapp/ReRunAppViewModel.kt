package br.org.cesar.wificonnect.ui.screens.rerunapp

import android.content.ComponentName
import android.content.ContentResolver
import android.content.Intent
import androidx.lifecycle.ViewModel
import br.org.cesar.wificonnect.BuildConfig
import br.org.cesar.wificonnect.domain.usecase.UseCaseListener
import br.org.cesar.wificonnect.domain.usecase.UseCaseStatus
import br.org.cesar.wificonnect.domain.usecase.accessibility.AccessibilityServiceUseCase
import br.org.cesar.wificonnect.domain.usecase.playstore.InstallAppUseCase
import br.org.cesar.wificonnect.domain.usecase.system.RunAppUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ReRunAppViewModel @Inject constructor(
    private val mInstallAppUseCase: InstallAppUseCase,
    private val mRunAppUseCase: RunAppUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(ReRunAppUiState())
    val uiState: StateFlow<ReRunAppUiState> = _uiState.asStateFlow()

    private val mUseCaseListener = object : UseCaseListener {
        override fun onUseCaseStarted() {
            updateState(isRunning = true)
        }

        override fun onUseCaseMsgReceived(msg: String) {
            updateState(listenerMessage = msg)
        }

        override fun onUseCaseSuccess() {
            updateState(
                isRunning = false,
                useCaseStatus = UseCaseStatus.SUCCESS
            )
        }

        override fun onUseCaseFailed(reason: String?) {
            updateState(
                isRunning = false,
                useCaseStatus = UseCaseStatus.ERROR
            )
        }
    }

    init {
        updateAppConfig()
    }

    fun onUiEvent(event: ReRunAppUiEvent) {
        when (event) {
            is ReRunAppUiEvent.UseCaseInitialize -> initUseCase()
            is ReRunAppUiEvent.UpdateAppIntent -> updateLastAppIntent(event.callback)
            is ReRunAppUiEvent.UpdateA11yComponentName -> updateState(a11yServiceComponentName = event.componentName)
            is ReRunAppUiEvent.CheckA11yState -> updateA11yState(event.contentResolver)
            is ReRunAppUiEvent.UseCaseStatusChanged -> updateUseCaseStatus(event.useCaseStatus)
            is ReRunAppUiEvent.UseCaseRunningStateChanged -> updateUseCaseRunningState(event.isRunning)
        }
    }

    private fun updateState(
        companyName: String? = null,
        packageName: String? = null,
        listenerMessage: String? = null,
        useCaseStatus: UseCaseStatus? = null,
        isRunning: Boolean? = null,
        a11yServiceComponentName: ComponentName? = null,
    ) {
        _uiState.update { currentState ->
            currentState.copy(
                companyName = companyName ?: currentState.companyName,
                packageName = packageName ?: currentState.packageName,
                listenerMessage = listenerMessage ?: currentState.listenerMessage,
                useCaseStatus = useCaseStatus ?: currentState.useCaseStatus,
                isRunning = isRunning ?: currentState.isRunning,
                a11yServiceComponentName = a11yServiceComponentName
                    ?: currentState.a11yServiceComponentName
            )
        }
    }

    private fun updateLastAppIntent(callback: (Intent?) -> Unit) {
        val appIntent = mRunAppUseCase.getForegroundAppIntent()
        mUseCaseListener.onUseCaseMsgReceived(appIntent?.component?.packageName ?: "NA")

        callback(appIntent)
    }

    private fun updateA11yState(
        contentResolver: ContentResolver,
    ) {
        val componentName = uiState.value.a11yServiceComponentName

        _uiState.update { currentState ->
            currentState.copy(
                isA11yEnabled = if (componentName != null) {
                    AccessibilityServiceUseCase.isServiceEnabled(contentResolver, componentName)
                } else {
                    null
                }
            )
        }
    }

    private fun updateUseCaseStatus(useCaseStatus: UseCaseStatus) {
        if (UseCaseStatus.SUCCESS == useCaseStatus) {
            mUseCaseListener.onUseCaseSuccess()
        } else if (UseCaseStatus.ERROR == useCaseStatus) {
            mUseCaseListener.onUseCaseFailed("Failed to run app")
        }
    }

    private fun updateUseCaseRunningState(isRunning: Boolean) {
        if (isRunning && !uiState.value.isRunning) {
            mInstallAppUseCase.clearResult()
        }

        updateState(isRunning = isRunning)
    }

    private fun initUseCase() {
        val currentState = uiState.value
        mInstallAppUseCase.apply {
            companyName = currentState.companyName
            pkgName = currentState.packageName
            useCaseListener = mUseCaseListener
            timeoutMillis = currentState.timeoutMillis
        }
    }

    private fun updateAppConfig() {
        updateState(
            companyName = uiState.value.companyName ?: BuildConfig.APP_COMPANY,
            packageName = uiState.value.packageName ?: BuildConfig.APP_PKG,
        )
    }
}