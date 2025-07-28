package br.org.cesar.wificonnect.ui.components.tiles.wechat

import android.app.Activity
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.window.layout.WindowInfoTracker
import br.org.cesar.wificonnect.ui.components.OverlayPermission
import br.org.cesar.wificonnect.ui.components.PrefsItem
import br.org.cesar.wificonnect.ui.components.RunIconButton
import br.org.cesar.wificonnect.ui.theme.DesignSystemTheme

@Composable
fun WeChatTileRoot(
    onA11yStateCheck: () -> Boolean?,
) {
    val viewModel = hiltViewModel<WeChatViewModel>()
    val uiState = viewModel.uiState.collectAsState().value

    WeChatTile(
        uiState = uiState,
        onUiEvent = viewModel::onUiEvent,
        onA11yStateCheck = onA11yStateCheck,
    )
}

@Composable
private fun WeChatTile(
    uiState: WeChatUiState,
    onUiEvent: (WeChatUiEvent) -> Unit,
    onA11yStateCheck: () -> Boolean?,
) {
    val context = LocalContext.current
    val activity = context as? Activity

    if (activity != null) {
        LaunchedEffect(Unit) {
            WindowInfoTracker.getOrCreate(activity)
                .windowLayoutInfo(activity)
                .collect { newLayoutInfo ->
                    onUiEvent(WeChatUiEvent.UpdateWindowLayoutInfo(newLayoutInfo))
                }
        }
    }

    OverlayPermission()

    PrefsItem(
        icon = { },
        text = { Text("WeChat") },
        secondaryText = { Text("") },
        trailing = {
            RunIconButton(
                isRunning = false,
                onClick = {
                    val isEnabled = onA11yStateCheck()
                    if (isEnabled == true) {
                        onUiEvent(WeChatUiEvent.ResetUseCase)
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
            WeChatTile(
                uiState = WeChatUiState(),
                onA11yStateCheck = { false },
                onUiEvent = {}
            )
        }
    }
}