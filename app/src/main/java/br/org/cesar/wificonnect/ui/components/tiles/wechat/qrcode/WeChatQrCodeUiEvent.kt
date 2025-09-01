package br.org.cesar.wificonnect.ui.components.tiles.wechat.qrcode;

sealed interface WeChatQrCodeUiEvent {
    data object ResetUseCase : WeChatQrCodeUiEvent
}
