package br.org.cesar.wificonnect.ui.components.tiles.wechat

import androidx.window.layout.WindowLayoutInfo

sealed interface WeChatUiEvent {
    data object ResetUseCase : WeChatUiEvent

    data class UpdateWindowLayoutInfo(val layoutInfo: WindowLayoutInfo) : WeChatUiEvent
}