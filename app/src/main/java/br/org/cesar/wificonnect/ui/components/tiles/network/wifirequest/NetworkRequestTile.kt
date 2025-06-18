package br.org.cesar.wificonnect.ui.components.tiles.network.wifirequest

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import br.org.cesar.wificonnect.domain.usecase.UseCaseStatus
import br.org.cesar.wificonnect.ui.components.PrefsItem
import br.org.cesar.wificonnect.ui.components.RunIconButton
import br.org.cesar.wificonnect.ui.navigation.AppNavDestination
import br.org.cesar.wificonnect.ui.theme.DesignSystemTheme

@Composable
fun NetworkRequestTileRoot(
    onA11yStateCheck: () -> Boolean?,
    navRoute: AppNavDestination.NetworkRequest = AppNavDestination.NetworkRequest(),
) {
    val viewModel = hiltViewModel<NetworkRequestViewModel>()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        if (navRoute.autoRun) {
            viewModel.onUiEvent(
                NetworkRequestUiEvent.WiFiRequest(
                    navRoute.wifiSsid,
                    navRoute.wifiPsk
                )
            )

            navRoute.autoRun = false
        }
    }

    NetworkRequestTile(
        uiState = uiState,
        onUiEvent = viewModel::onUiEvent,
        onA11yStateCheck = onA11yStateCheck
    )
}

@Composable
private fun NetworkRequestTile(
    uiState: NetworkRequestUiState,
    onUiEvent: (NetworkRequestUiEvent) -> Unit,
    onA11yStateCheck: () -> Boolean?,
) {
    RequestPermission(uiState, onUiEvent)
    NetworkRequestSetup(uiState)

    val secondaryText = if (UseCaseStatus.SUCCESS != uiState.useCaseStatus) {
        uiState.listenerMessage
    } else {
        uiState.getFormattedRequestDuration()
    }

    PrefsItem(
        icon = { NetworkRequestStatusIcon(uiState) },
        text = { Text("Request Wi-Fi connection") },
        secondaryText = { Text(secondaryText) },
        trailing = {
            NetworkRequestActionIcon(uiState, onUiEvent, onA11yStateCheck)
        }
    )
}

@Composable
private fun NetworkRequestStatusIcon(
    uiState: NetworkRequestUiState,
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
private fun NetworkRequestActionIcon(
    uiState: NetworkRequestUiState,
    onUiEvent: (NetworkRequestUiEvent) -> Unit,
    onA11yStateCheck: () -> Boolean?,
) {
    RunIconButton(
        isRunning = uiState.isRunning,
        onClick = {
            onA11yStateCheck()
            if (!uiState.isRunning) {
                onUiEvent(
                    NetworkRequestUiEvent.WiFiRequest(
                        uiState.wifiSsid,
                        uiState.wifiPsk
                    )
                )
            }
        },
    )
}

@Composable
private fun NetworkRequestSetup(
    uiState: NetworkRequestUiState,
) {
    val context = LocalContext.current

    LaunchedEffect(uiState.isWifiEnabled) {
        if (!uiState.isWifiEnabled) {
            val panelIntent = Intent(Settings.Panel.ACTION_WIFI)
            context.startActivity(panelIntent)
        }
    }
}

@Composable
private fun RequestPermission(
    uiState: NetworkRequestUiState,
    onUiEvent: (NetworkRequestUiEvent) -> Unit
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(context, "Permission denied!", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        val permissionStatus = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        )
        onUiEvent(NetworkRequestUiEvent.UpdatePermissionStatus(permissionStatus))
    }

    LaunchedEffect(uiState.permissionStatus) {
        if (uiState.permissionStatus != PackageManager.PERMISSION_GRANTED) {
            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
}

@Preview
@Composable
private fun NetworkRequestTilePreview() {
    DesignSystemTheme {
        Surface {
            NetworkRequestTile(
                uiState = NetworkRequestUiState(),
                onUiEvent = {},
                onA11yStateCheck = { false }
            )
        }
    }
}