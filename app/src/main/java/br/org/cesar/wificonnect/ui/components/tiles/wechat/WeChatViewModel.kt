package br.org.cesar.wificonnect.ui.components.tiles.wechat

import androidx.lifecycle.ViewModel
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowLayoutInfo
import br.org.cesar.wificonnect.domain.usecase.wechat.WeChatUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class WeChatViewModel @Inject constructor(
    private val mUseCase: WeChatUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(WeChatUiState())
    val uiState: StateFlow<WeChatUiState> = _uiState.asStateFlow()

    fun onUiEvent(event: WeChatUiEvent) {
        when (event) {
            is WeChatUiEvent.ResetUseCase -> {
                mUseCase.press = 0
            }

            is WeChatUiEvent.UpdateWindowLayoutInfo -> updateWindowLayoutInfo(event.layoutInfo)
        }
    }

    private fun updateState(
        packageName: String? = null,
        windowLayoutInfo: WindowLayoutInfo? = null
    ) {
        _uiState.update { currentState ->
            currentState.copy(
                packageName = packageName ?: currentState.packageName,
                windowLayoutInfo = windowLayoutInfo ?: currentState.windowLayoutInfo
            )
        }
    }

    private fun updateWindowLayoutInfo(layoutInfo: WindowLayoutInfo) {
        updateState(windowLayoutInfo = layoutInfo)
        mUseCase.isFoldable = layoutInfo.displayFeatures.any { it is FoldingFeature }
    }
}