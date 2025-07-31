package br.org.cesar.wificonnect.ui.components.tiles.dialer.incoming

import android.telecom.Call
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
    DialerRoleHold(uiState, viewModel::onUiEvent)

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
    PrefsItem(
        icon = { },
        text = { Text("Incoming Call") },
        secondaryText = { Text("") },
        trailing = {
            ActionButton(uiState, onUiEvent)
        }
    )
}

@Composable
private fun ActionButton(
    uiState: DialerUiState,
    onUiEvent: (DialerUiEvent) -> Unit,
) {
    if (uiState.callDirection == Call.Details.DIRECTION_UNKNOWN) {
        RunIconButton(
            isRunning = false,
            onClick = {
                onUiEvent(DialerUiEvent.UpdateCallDirection(Call.Details.DIRECTION_INCOMING))
            },
            enabled = uiState.isDefaultDialer
        )
    } else if (uiState.callDirection == Call.Details.DIRECTION_INCOMING) {
        Box {
            CircularProgressIndicator(
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )

            IconButton(
                onClick = {
                    onUiEvent(DialerUiEvent.UpdateCallDirection(Call.Details.DIRECTION_UNKNOWN))
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Stop,
                    contentDescription = "Stop Use Case"
                )
            }
        }
    }
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