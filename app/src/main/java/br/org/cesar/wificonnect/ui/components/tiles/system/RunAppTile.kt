package br.org.cesar.wificonnect.ui.components.tiles.system

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import br.org.cesar.wificonnect.domain.usecase.UseCaseStatus
import br.org.cesar.wificonnect.ui.components.PrefsItem
import br.org.cesar.wificonnect.ui.components.RunIconButton
import br.org.cesar.wificonnect.ui.theme.DesignSystemTheme

@Composable
fun RunAppTileRoot() {
    val viewModel = hiltViewModel<RunAppViewModel>()
    val uiState = viewModel.uiState.collectAsState().value

    RunAppTile(
        uiState = uiState,
        onUiEvent = viewModel::onUiEvent
    )
}

@Composable
private fun RunAppTile(
    uiState: RunAppUiState,
    onUiEvent: (RunAppUiEvent) -> Unit,
) {
    val context = LocalContext.current

    val secondaryText = if (UseCaseStatus.SUCCESS != uiState.useCaseStatus) {
        uiState.listenerMessage
    } else {
        ""
    }

    PrefsItem(
        icon = { RunAppStatusIcon(uiState) },
        text = { Text("Re-run the last app") },
        secondaryText = { Text(secondaryText) },
        trailing = {
            RunIconButton(
                isRunning = uiState.isRunning,
                onClick = {
                    onUiEvent(
                        RunAppUiEvent.UpdateAppIntent { launchIntent ->
                            if (launchIntent != null) {
                                context.startActivity(launchIntent)
                            }
                        }
                    )
                }
            )
        }
    )
}

@Composable
private fun RunAppStatusIcon(
    uiState: RunAppUiState,
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

@Preview
@Composable
private fun RunAppTileRootPreview() {
    DesignSystemTheme {
        Surface {
            RunAppTile(
                uiState = RunAppUiState(),
                onUiEvent = {}
            )
        }
    }
}