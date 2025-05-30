package br.org.cesar.wificonnect.ui.screens.network

import android.Manifest
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
import br.org.cesar.wificonnect.ui.components.PrefsItem
import br.org.cesar.wificonnect.ui.theme.DesignSystemTheme

@Composable
fun NetworkRequestScreenRoot() {
    val viewModel = hiltViewModel<NetworkRequestViewModel>()
    val uiState by viewModel.uiState.collectAsState()

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

    val context = LocalContext.current
    LaunchedEffect(uiState.isWifiEnabled) {
        if (!uiState.isWifiEnabled) {
            val panelIntent = Intent(Settings.Panel.ACTION_WIFI)
            context.startActivity(panelIntent)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Use Cases")
                }
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
                PrefsItem(
                    icon = {
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
                    },
                    text = { Text("Request Wi-Fi Connection") },
                    secondaryText = {
                        Text(
                            if (UseCaseStatus.SUCCESS != uiState.useCaseStatus) {
                                uiState.listenerMessage
                            } else {
                                uiState.getFormattedRequestDuration()
                            }
                        )
                    },
                    trailing = {
                        IconButton(
                            onClick = {
                                onUiEvent(
                                    NetworkRequestUiEvent.WiFiRequest(
                                        uiState.wifiSsid,
                                        uiState.wifiPsk,
                                        uiState.useCaseListener
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
                )
            }
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
        NetworkRequestScreenRoot()
    }
}