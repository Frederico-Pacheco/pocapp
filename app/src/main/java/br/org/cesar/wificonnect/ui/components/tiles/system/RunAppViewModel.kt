package br.org.cesar.wificonnect.ui.components.tiles.system

import android.content.Intent
import androidx.lifecycle.ViewModel
import br.org.cesar.wificonnect.domain.usecase.UseCaseListener
import br.org.cesar.wificonnect.domain.usecase.UseCaseStatus
import br.org.cesar.wificonnect.domain.usecase.system.RunAppUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class RunAppViewModel @Inject constructor(
    private val mRunAppUseCase: RunAppUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(RunAppUiState())
    val uiState: StateFlow<RunAppUiState> = _uiState.asStateFlow()

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

    fun onUiEvent(event: RunAppUiEvent) {
        when (event) {
            is RunAppUiEvent.UpdateAppIntent -> updateLastAppIntent(event.callback)
        }
    }

    private fun updateState(
        listenerMessage: String? = null,
        useCaseStatus: UseCaseStatus? = null,
        isRunning: Boolean? = null,
    ) {
        _uiState.update { currentState ->
            currentState.copy(
                listenerMessage = listenerMessage ?: currentState.listenerMessage,
                useCaseStatus = useCaseStatus ?: currentState.useCaseStatus,
                isRunning = isRunning ?: currentState.isRunning,
            )
        }
    }

    private fun updateLastAppIntent(callback: (Intent?) -> Unit) {
        mUseCaseListener.onUseCaseStarted()
        val appIntent = mRunAppUseCase.getForegroundAppIntent()
        mUseCaseListener.onUseCaseMsgReceived(appIntent?.component?.packageName ?: "NA")

        callback(appIntent)

        if (appIntent != null) {
            mUseCaseListener.onUseCaseSuccess()
        } else {
            mUseCaseListener.onUseCaseFailed("No app found.")
        }
    }
}