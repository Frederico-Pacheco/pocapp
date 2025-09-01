package br.org.cesar.wificonnect.ui.components.tiles.wechat.qrcode

import androidx.lifecycle.ViewModel
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowLayoutInfo
import br.org.cesar.wificonnect.domain.usecase.wechat.WeChatQrCodeUseCase
import br.org.cesar.wificonnect.domain.usecase.wechat.WeChatUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class WeChatQrCodeViewModel @Inject constructor(
    private val mUseCase: WeChatQrCodeUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(WeChatQrCodeUiState())
    val uiState: StateFlow<WeChatQrCodeUiState> = _uiState.asStateFlow()

    fun onUiEvent(event: WeChatQrCodeUiEvent) {
        when (event) {
            is WeChatQrCodeUiEvent.ResetUseCase -> {
                mUseCase.press = 0
            }
        }
    }

    private fun updateState(
        packageName: String? = null,
    ) {
        _uiState.update { currentState ->
            currentState.copy(
                packageName = packageName ?: currentState.packageName
            )
        }
    }
}