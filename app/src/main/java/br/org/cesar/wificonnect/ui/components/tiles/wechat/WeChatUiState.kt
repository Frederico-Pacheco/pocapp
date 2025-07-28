package br.org.cesar.wificonnect.ui.components.tiles.wechat

import androidx.window.layout.WindowLayoutInfo

data class WeChatUiState(
    val packageName: String = "com.tencent.mm",
    val windowLayoutInfo: WindowLayoutInfo? = null
)