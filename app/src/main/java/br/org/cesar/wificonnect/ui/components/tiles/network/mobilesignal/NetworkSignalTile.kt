package br.org.cesar.wificonnect.ui.components.tiles.network.mobilesignal

import android.Manifest
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import br.org.cesar.wificonnect.domain.usecase.UseCaseStatus
import br.org.cesar.wificonnect.ui.components.PrefsItem
import br.org.cesar.wificonnect.ui.components.RequestPermission
import br.org.cesar.wificonnect.ui.components.RunIconButton
import br.org.cesar.wificonnect.ui.theme.DesignSystemTheme

@Composable
fun NetworkSignalTileRoot() {
    val viewModel = hiltViewModel<NetworkSignalViewModel>()
    val uiState by viewModel.uiState.collectAsState()

    NetworkSignalTile(
        uiState = uiState,
        onUiEvent = viewModel::onUiEvent
    )
}

@Composable
private fun NetworkSignalTile(
    uiState: NetworkSignalUiState,
    onUiEvent: (NetworkSignalUiEvent) -> Unit
) {
    RequestPermission(
        Manifest.permission.READ_PHONE_STATE,
        uiState.permissionStatus,
        onPermissionChange = { permissionStatus ->
            onUiEvent(NetworkSignalUiEvent.UpdatePermissionStatus(permissionStatus))
        }
    )

    PrefsItem(
        icon = { NetworkSignalStatusIcon(uiState) },
        text = { Text("Measure mobile network signal") },
        secondaryText = { Text("") },
        trailing = { NetworkSignalActionIcon(uiState, onUiEvent) }
    )
}

@Composable
private fun NetworkSignalStatusIcon(
    uiState: NetworkSignalUiState,
) {
    when (uiState.useCaseStatus) {
        UseCaseStatus.SUCCESS -> {
            Icon(
                Icons.Default.Check,
                contentDescription = "Success"
            )
        }

        UseCaseStatus.ERROR -> {
            Icon(
                Icons.Default.Close,
                tint = Color.Red,
                contentDescription = "Error"
            )
        }

        UseCaseStatus.NOT_EXECUTED -> {}
    }
}

@Composable
private fun NetworkSignalActionIcon(
    uiState: NetworkSignalUiState,
    onUiEvent: (NetworkSignalUiEvent) -> Unit
) {
    RunIconButton(
        isRunning = uiState.isRunning,
        onClick = {
            if (!uiState.isRunning) {
                onUiEvent(NetworkSignalUiEvent.MeasureNetworkSignal)
            }
        }
    )
}

@Preview
@Composable
private fun NetworkSignalTilePreview() {
    DesignSystemTheme {
        Surface {
            NetworkSignalTile(
                uiState = NetworkSignalUiState(),
                onUiEvent = {}
            )
        }
    }
}