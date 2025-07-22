package br.org.cesar.wificonnect.ui.components.tiles.dialer.component

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import br.org.cesar.wificonnect.ui.components.tiles.dialer.DialerUiEvent
import br.org.cesar.wificonnect.ui.components.tiles.dialer.DialerUiState


@Composable
fun DialerRoleHold(
    uiState: DialerUiState,
    onUiEvent: (DialerUiEvent) -> Unit,
) {
    LifecycleEvents(onUiEvent)
    PermissionHandler(uiState, onUiEvent)
}

@Composable
private fun LifecycleEvents(
    onUiEvent: (DialerUiEvent) -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> {
                    onUiEvent(DialerUiEvent.RegisterDialerChangedReceiver)
                }

                Lifecycle.Event.ON_DESTROY -> {
                    onUiEvent(DialerUiEvent.UnregisterDialerChangedReceiver)
                }

                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

@Composable
private fun PermissionHandler(
    uiState: DialerUiState,
    onUiEvent: (DialerUiEvent) -> Unit,
) {
    val requestRoleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            onUiEvent(DialerUiEvent.CheckDefaultDialerStatus)
        }
    }

    LaunchedEffect(uiState.roleIntent) {
        uiState.roleIntent?.let { requestRoleLauncher.launch(it) }
    }
}