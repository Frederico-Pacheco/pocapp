package br.org.cesar.wificonnect.ui.screens.network

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import br.org.cesar.wificonnect.domain.usecase.UseCaseStatus
import br.org.cesar.wificonnect.service.PocAccessibilityService
import br.org.cesar.wificonnect.ui.components.PrefsItem
import br.org.cesar.wificonnect.ui.navigation.AppNavDestination
import br.org.cesar.wificonnect.ui.navigation.NavManager
import br.org.cesar.wificonnect.ui.theme.DesignSystemTheme

@Composable
fun NetworkRequestScreenRoot(
    navManager: NavManager,
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
        }
    }

    NetworkRequestScreen(
        uiState = uiState,
        onUiEvent = viewModel::onUiEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NetworkRequestScreen(
    uiState: NetworkRequestUiState,
    onUiEvent: (NetworkRequestUiEvent) -> Unit
) {
    RequestPermission(uiState, onUiEvent)

    // FIXME: call NetworkRequestSetup only on action button click
    NetworkRequestSetup(uiState, onUiEvent)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Use Cases") }
            )
        }
    ) { innerPadding ->
        Box(
            Modifier.padding(innerPadding)
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val secondaryText = if (UseCaseStatus.SUCCESS != uiState.useCaseStatus) {
                    uiState.listenerMessage
                } else {
                    uiState.getFormattedRequestDuration()
                }

                PrefsItem(
                    icon = { NetworkRequestStatusIcon(uiState) },
                    text = { Text("Request Wi-Fi Connection") },
                    secondaryText = { Text(secondaryText) },
                    trailing = { NetworkRequestActionIcon(uiState, onUiEvent) }
                )
            }
        }
    }
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
    onUiEvent: (NetworkRequestUiEvent) -> Unit
) {
    IconButton(
        onClick = {
            onUiEvent(
                NetworkRequestUiEvent.WiFiRequest(
                    uiState.wifiSsid,
                    uiState.wifiPsk
                )
            )
        }
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator()
        } else {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Run Use Case"
            )
        }
    }
}

@Composable
private fun NetworkRequestSetup(
    uiState: NetworkRequestUiState,
    onUiEvent: (NetworkRequestUiEvent) -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(uiState.isWifiEnabled) {
        if (!uiState.isWifiEnabled) {
            val panelIntent = Intent(Settings.Panel.ACTION_WIFI)
            context.startActivity(panelIntent)
        }
    }

    LaunchedEffect(Unit) {
        val expectedComponentName = ComponentName(context, PocAccessibilityService::class.java)
        val enabledServicesSetting = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )

        onUiEvent(
            NetworkRequestUiEvent.VerifyAccessibilityServiceEnabled(
                enabledServicesSetting, expectedComponentName
            )
        )
    }

    LaunchedEffect(uiState.isAccessibilityServiceEnabled) {
        if (!uiState.isAccessibilityServiceEnabled) {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            //context.startActivity(intent) FIXME: launch intent
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
private fun NetworkRequestScreenPreview() {
    DesignSystemTheme {
        NetworkRequestScreen(
            uiState = NetworkRequestUiState(),
            onUiEvent = {}
        )
    }
}