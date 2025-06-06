package br.org.cesar.wificonnect.ui.screens.tiles.playstore

import android.content.ComponentName
import android.content.Intent
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import br.org.cesar.wificonnect.domain.usecase.UseCaseStatus
import br.org.cesar.wificonnect.ui.components.EnableAccessibilityService
import br.org.cesar.wificonnect.ui.components.PrefsItem
import br.org.cesar.wificonnect.ui.components.RunIconButton
import br.org.cesar.wificonnect.ui.theme.DesignSystemTheme

@Composable
fun InstallAppTileRoot() {
    val viewModel = hiltViewModel<InstallAppViewModel>()
    val uiState by viewModel.uiState.collectAsState()

    InstallAppTile(
        uiState = uiState,
        onUiEvent = viewModel::onUiEvent
    )
}

@Composable
private fun InstallAppTile(
    uiState: InstallAppUiState,
    onUiEvent: (InstallAppUiEvent) -> Unit
) {
    InstallAppSetup(
        uiState = uiState,
        onUiEvent = onUiEvent
    )

    val secondaryText = if (UseCaseStatus.SUCCESS != uiState.useCaseStatus) {
        uiState.listenerMessage
    } else {
        uiState.getFormattedRequestDuration()
    }

    PrefsItem(
        icon = { InstallAppStatusIcon(uiState) },
        text = { Text("Install App") },
        secondaryText = { Text(secondaryText) },
        trailing = { InstallAppActionIcon(uiState, onUiEvent) }
    )
}

@Composable
private fun InstallAppSetup(
    uiState: InstallAppUiState,
    onUiEvent: (InstallAppUiEvent) -> Unit
) {
    if (uiState.canOpenAccessibilitySettings) {
        EnableAccessibilityService(
            isAccessibilityServiceEnabled = uiState.isAccessibilityServiceEnabled,
            onVerify = { enabledServicesSetting: String?, expectedComponentName: ComponentName ->
                onUiEvent(
                    InstallAppUiEvent.VerifyAccessibilityServiceEnabled(
                        enabledServicesSetting, expectedComponentName
                    )
                )

                onUiEvent(InstallAppUiEvent.UpdateAccessibilitySettingsAccess(false))
            }
        )
    }
}

@Composable
private fun InstallAppStatusIcon(
    uiState: InstallAppUiState,
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
private fun InstallAppActionIcon(
    uiState: InstallAppUiState,
    onUiEvent: (InstallAppUiEvent) -> Unit
) {
    val context = LocalContext.current

    RunIconButton(
        isRunning = uiState.isRunning,
        onClick = {
            onUiEvent(InstallAppUiEvent.Initialize)

            if (uiState.isAccessibilityServiceEnabled == true) {
                onUiEvent(InstallAppUiEvent.RunningStateChanged(true))
                uiState.packageName?.let { packageName ->
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = "market://details?id=${packageName}".toUri()
                        setPackage("com.android.vending")
                    }
                    context.startActivity(intent)
                }
            } else {
                onUiEvent(InstallAppUiEvent.UpdateAccessibilitySettingsAccess(true))
            }
        }
    )
}

@Preview
@Composable
private fun InstallAppTilePreview() {
    DesignSystemTheme {
        Surface {
            InstallAppTile(
                uiState = InstallAppUiState(),
                onUiEvent = {}
            )
        }
    }
}