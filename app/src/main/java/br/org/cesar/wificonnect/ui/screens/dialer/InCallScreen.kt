package br.org.cesar.wificonnect.ui.screens.dialer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import br.org.cesar.wificonnect.ui.components.tiles.dialer.DialerUiEvent
import br.org.cesar.wificonnect.ui.components.tiles.dialer.DialerUiState
import br.org.cesar.wificonnect.ui.components.tiles.dialer.DialerViewModel
import br.org.cesar.wificonnect.ui.theme.DesignSystemTheme

@Composable
fun InCallScreenRoot(
    callNumber: String?,
    onFinishActivity: () -> Unit
) {
    val viewModel = hiltViewModel<DialerViewModel>()
    val uiState by viewModel.uiState.collectAsState()

    InCallScreen(
        callNumber = callNumber ?: "Unknown number",
        uiState = uiState,
        onUiEvent = viewModel::onUiEvent,
        onFinishActivity = onFinishActivity
    )
}

@Composable
private fun InCallScreen(
    callNumber: String,
    uiState: DialerUiState,
    onUiEvent: (DialerUiEvent) -> Unit,
    onFinishActivity: () -> Unit
) {
    LaunchedEffect(uiState.callState) {
        if (uiState.canFinishActivity()) onFinishActivity()
    }

    Scaffold { innerPadding ->
        Box(Modifier.padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = callNumber,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = uiState.getCallStateText(),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                Row {
                    IconButton(
                        onClick = {
                            onUiEvent(DialerUiEvent.UpdateSpeakerState(!uiState.isSpeakerOn))
                        },
                        enabled = uiState.isSpeakerButtonEnabled(),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Icon(
                            imageVector = if (uiState.isSpeakerOn) {
                                Icons.AutoMirrored.Filled.VolumeUp
                            } else {
                                Icons.AutoMirrored.Filled.VolumeOff
                            },
                            contentDescription = null,
                            tint = if (uiState.isSpeakerOn) {
                                Color.Green
                            } else if (uiState.isSpeakerButtonEnabled()) {
                                MaterialTheme.colorScheme.onBackground
                            } else {
                                Color.Gray
                            }
                        )
                    }

                    IconButton(
                        onClick = { onUiEvent(DialerUiEvent.StartCall) },
                        enabled = uiState.isRinging(),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = null,
                            tint = if (uiState.isRinging()) Color.Green else Color.Gray
                        )
                    }

                    IconButton(
                        onClick = {
                            onUiEvent(DialerUiEvent.EndCall)
                            onFinishActivity()
                        },
                        enabled = uiState.isEndCallButtonEnabled(),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CallEnd,
                            contentDescription = null,
                            tint = if (uiState.isEndCallButtonEnabled()) Color.Red else Color.Gray
                        )
                    }
                }

            }
        }
    }
}

@Preview
@Composable
private fun UseCaseListScreenPreview() {
    DesignSystemTheme {
        InCallScreen(
            callNumber = "1234567890",
            uiState = DialerUiState(),
            onUiEvent = {},
            onFinishActivity = {}
        )
    }
}