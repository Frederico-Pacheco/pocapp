package br.org.cesar.wificonnect.ui.screens.rerunapp

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import br.org.cesar.wificonnect.domain.usecase.UseCaseStatus
import br.org.cesar.wificonnect.service.PocAccessibilityService
import br.org.cesar.wificonnect.ui.components.PrefsItem
import br.org.cesar.wificonnect.ui.components.RunIconButton
import br.org.cesar.wificonnect.ui.navigation.NavManager
import br.org.cesar.wificonnect.ui.theme.DesignSystemTheme

@Composable
fun ReRunAppScreenRoot(
    navManager: NavManager,
) {
    val viewModel = hiltViewModel<ReRunAppViewModel>()
    val uiState = viewModel.uiState.collectAsState().value

    ReRunAppScreen(
        uiState = uiState,
        onUiEvent = viewModel::onUiEvent,
        onNavigateUp = navManager::navigateUp
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReRunAppScreen(
    uiState: ReRunAppUiState,
    onUiEvent: (ReRunAppUiEvent) -> Unit,
    onNavigateUp: () -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()

    LaunchedEffect(Unit) {
        onUiEvent(
            ReRunAppUiEvent.UpdateA11yComponentName(
                ComponentName(context, PocAccessibilityService::class.java)
            )
        )
    }

    LaunchedEffect(lifecycleState) {
        when (lifecycleState) {
            Lifecycle.State.RESUMED -> {
                onUiEvent(ReRunAppUiEvent.CheckA11yState(context.contentResolver))
                onUiEvent(
                    ReRunAppUiEvent.UpdateAppIntent { launchIntent ->
                        if (launchIntent != null && uiState.isRunning) {
                            context.startActivity(launchIntent)
                        }
                    }
                )
            }

            else -> {}
        }
    }

    val secondaryText = if (UseCaseStatus.SUCCESS != uiState.useCaseStatus) {
        uiState.listenerMessage
    } else {
        ""
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                title = { Text("Re-run App") },
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
                    icon = { RunAppStatusIcon(uiState) },
                    text = { Text("Re-run The Last App") },
                    secondaryText = { Text(secondaryText) },
                    trailing = {
                        RunIconButton(
                            isRunning = uiState.isRunning,
                            onClick = {
                                installApp(context, uiState, onUiEvent)
                            }
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun RunAppStatusIcon(
    uiState: ReRunAppUiState,
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
    uiState: ReRunAppUiState,
    onUiEvent: (ReRunAppUiEvent) -> Unit,
) {
    onUiEvent(ReRunAppUiEvent.UseCaseInitialize)

    if (uiState.isA11yEnabled == true) {
        onUiEvent(ReRunAppUiEvent.AppRunningStateChanged(true))
        uiState.packageName?.let { packageName ->
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = "market://details?id=${packageName}".toUri()
                setPackage("com.android.vending")
            }
            context.startActivity(intent)
        }
    } else {
        context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
    }
}

@Preview
@Composable
private fun ReRunAppScreenPreview() {
    DesignSystemTheme {
        ReRunAppScreen(
            uiState = ReRunAppUiState(),
            onUiEvent = {},
            onNavigateUp = {}
        )
    }
}