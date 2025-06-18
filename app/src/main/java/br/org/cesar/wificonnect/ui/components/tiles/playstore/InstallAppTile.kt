package br.org.cesar.wificonnect.ui.components.tiles.playstore

import android.content.Context
import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import br.org.cesar.wificonnect.domain.usecase.UseCaseStatus
import br.org.cesar.wificonnect.ui.components.PrefsItem
import br.org.cesar.wificonnect.ui.components.RunIconButton
import br.org.cesar.wificonnect.ui.navigation.AppNavDestination
import br.org.cesar.wificonnect.ui.theme.DesignSystemTheme

@Composable
fun InstallAppTileRoot(
    onA11yStateCheck: () -> Boolean?,
    navRoute: AppNavDestination.PlayStoreInstall = AppNavDestination.PlayStoreInstall(),
) {
    val viewModel = hiltViewModel<InstallAppViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if (navRoute.autoRun) {
            installApp(context, uiState, viewModel::onUiEvent, onA11yStateCheck)

            navRoute.autoRun = false
        }
    }

    InstallAppTile(
        uiState = uiState,
        onUiEvent = viewModel::onUiEvent,
        onA11yStateCheck = onA11yStateCheck,
    )
}

@Composable
private fun InstallAppTile(
    uiState: InstallAppUiState,
    onUiEvent: (InstallAppUiEvent) -> Unit,
    onA11yStateCheck: () -> Boolean?,
) {
    val context = LocalContext.current

    val secondaryText = if (UseCaseStatus.SUCCESS != uiState.useCaseStatus) {
        uiState.listenerMessage
    } else {
        uiState.getFormattedRequestDuration()
    }

    PrefsItem(
        icon = { InstallAppStatusIcon(uiState) },
        text = { Text("Install app") },
        secondaryText = { Text(secondaryText) },
        trailing = {
            RunIconButton(
                isRunning = uiState.isRunning,
                onClick = { installApp(context, uiState, onUiEvent, onA11yStateCheck) }
            )
        }
    )
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

private fun installApp(
    context: Context,
    uiState: InstallAppUiState,
    onUiEvent: (InstallAppUiEvent) -> Unit,
    onA11yStateCheck: () -> Boolean?,
) {
    val isEnabled = onA11yStateCheck()
    onUiEvent(InstallAppUiEvent.Initialize)
    if (isEnabled == true) {
        onUiEvent(InstallAppUiEvent.RunningStateChanged(true))
        uiState.packageName?.let { packageName ->
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = "market://details?id=${packageName}".toUri()
                setPackage("com.android.vending")
            }
            context.startActivity(intent)
        }
    }
}

@Preview
@Composable
private fun InstallAppTilePreview() {
    DesignSystemTheme {
        Surface {
            InstallAppTile(
                uiState = InstallAppUiState(),
                onUiEvent = {},
                onA11yStateCheck = { false },
            )
        }
    }
}