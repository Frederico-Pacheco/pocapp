package br.org.cesar.wificonnect.ui.screens.dialer

import android.telecom.Call
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import br.org.cesar.wificonnect.domain.usecase.dialer.IncomingCall.DEFAULT_STATE
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
        val endCallStates = setOf(DEFAULT_STATE, Call.STATE_DISCONNECTED)
        if (uiState.callState in endCallStates) {
            onFinishActivity()
        }
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
                Button(
                    onClick = { onUiEvent(DialerUiEvent.StartCall) },
                    enabled = (uiState.callState == Call.STATE_RINGING)
                ) {
                    Text("Start Call")
                }

                val endCallEnabledStates = setOf(
                    Call.STATE_DIALING,
                    Call.STATE_RINGING,
                    Call.STATE_ACTIVE
                )
                Button(
                    onClick = {
                        onUiEvent(DialerUiEvent.EndCall)
                        onFinishActivity()
                    },
                    enabled = (uiState.callState in endCallEnabledStates)
                ) {
                    Text("End Call")
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