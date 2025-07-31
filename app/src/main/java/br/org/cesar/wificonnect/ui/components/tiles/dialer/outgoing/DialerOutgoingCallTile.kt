package br.org.cesar.wificonnect.ui.components.tiles.dialer.outgoing

import android.telecom.Call
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
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
fun DialerOutgoingCallTileRoot() {
    val dialerViewModel = hiltViewModel<DialerViewModel>()
    val dialerUiState by dialerViewModel.uiState.collectAsStateWithLifecycle()
    DialerRoleHold(dialerUiState, dialerViewModel::onUiEvent)

    val viewModel = hiltViewModel<DialerOutgoingCallViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DialerOutgoingCallTile(
        uiState = uiState,
        onUiEvent = viewModel::onUiEvent,
        uiDialerState = dialerUiState,
        onDialerUiEvent = dialerViewModel::onUiEvent
    )
}

@Composable
private fun DialerOutgoingCallTile(
    uiState: DialerOutgoingCallUiState,
    onUiEvent: (DialerOutgoingCallUiEvent) -> Unit,
    uiDialerState: DialerUiState,
    onDialerUiEvent: (DialerUiEvent) -> Unit,
) {
    val context = LocalContext.current
    LaunchedEffect(uiState.dialIntent) {
        uiState.dialIntent?.let { context.startActivity(it) }
    }

    PrefsItem(
        icon = { },
        text = { Text("Outgoing Call") },
        secondaryText = { Text("") },
        trailing = {
            RunIconButton(
                isRunning = uiDialerState.callDirection == Call.Details.DIRECTION_OUTGOING,
                onClick = {
                    onDialerUiEvent(
                        DialerUiEvent.UpdateCallDirection(Call.Details.DIRECTION_OUTGOING)
                    )
                    onUiEvent(DialerOutgoingCallUiEvent.UpdateDialIntent)
                },
            )
        }
    )
}

@Preview
@Composable
private fun ScrollReelsTilePreview() {
    DesignSystemTheme {
        Surface {
            DialerOutgoingCallTile(
                uiState = DialerOutgoingCallUiState(),
                onUiEvent = {},
                uiDialerState = DialerUiState(),
                onDialerUiEvent = {}
            )
        }
    }
}