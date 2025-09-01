package br.org.cesar.wificonnect.ui.components.tiles.wechat.qrcode

import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import br.org.cesar.wificonnect.ui.components.OverlayPermission
import br.org.cesar.wificonnect.ui.components.PrefsItem
import br.org.cesar.wificonnect.ui.components.RunIconButton
import br.org.cesar.wificonnect.ui.theme.DesignSystemTheme

@Composable
fun WeChatQrCodeTileRoot(
    onA11yStateCheck: () -> Boolean?,
) {
    val viewModel = hiltViewModel<WeChatQrCodeViewModel>()
    val uiState = viewModel.uiState.collectAsState().value

    WeChatQrCodeTile(
        uiState = uiState,
        onUiEvent = viewModel::onUiEvent,
        onA11yStateCheck = onA11yStateCheck,
    )
}

@Composable
private fun WeChatQrCodeTile(
    uiState: WeChatQrCodeUiState,
    onUiEvent: (WeChatQrCodeUiEvent) -> Unit = {},
    onA11yStateCheck: () -> Boolean?,
) {
    val context = LocalContext.current
    OverlayPermission()

    PrefsItem(
        icon = { },
        text = { Text("WeChat QR Code") },
        secondaryText = { Text("") },
        trailing = {
            RunIconButton(
                isRunning = false,
                onClick = {
                    val isEnabled = onA11yStateCheck()
                    if (isEnabled == true) {
                        onUiEvent(WeChatQrCodeUiEvent.ResetUseCase)
                        val launchIntent =
                            context.packageManager.getLaunchIntentForPackage(uiState.packageName)
                        launchIntent?.setPackage(uiState.packageName)
                        context.startActivity(launchIntent)
                    }
                }
            )
        }
    )
}

@Preview
@Composable
private fun WeChatTilePreview() {
    DesignSystemTheme {
        Surface {
            WeChatQrCodeTile(
                uiState = WeChatQrCodeUiState(),
                onA11yStateCheck = { false },
                onUiEvent = {}
            )
        }
    }
}