package br.org.cesar.wificonnect.ui.components.tiles.dialer.incoming

import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.org.cesar.wificonnect.ui.components.PrefsItem
import br.org.cesar.wificonnect.ui.components.RunIconButton
import br.org.cesar.wificonnect.ui.components.tiles.dialer.DialerUiEvent
import br.org.cesar.wificonnect.ui.components.tiles.dialer.DialerUiState
import br.org.cesar.wificonnect.ui.components.tiles.dialer.DialerViewModel
import br.org.cesar.wificonnect.ui.components.tiles.dialer.component.DialerRoleHold
import br.org.cesar.wificonnect.ui.theme.DesignSystemTheme

@Composable
fun DialerIncomingCallTileRoot() {
    val viewModel = hiltViewModel<DialerViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DialerIncomingCallTile(
        uiState = uiState,
        onUiEvent = viewModel::onUiEvent,
    )
}

@Composable
private fun DialerIncomingCallTile(
    uiState: DialerUiState,
    onUiEvent: (DialerUiEvent) -> Unit,
) {
    DialerRoleHold(uiState, onUiEvent)

    PrefsItem(
        icon = { },
        text = { Text("Incoming Call") },
        secondaryText = { Text("") },
        trailing = {
            RunIconButton(
                isRunning = false,
                onClick = { },
                enabled = uiState.isDefaultDialer
            )
        }
    )
}

@Preview
@Composable
private fun ScrollReelsTilePreview() {
    DesignSystemTheme {
        Surface {
            DialerIncomingCallTile(
                uiState = DialerUiState(),
                onUiEvent = {},
            )
        }
    }
}