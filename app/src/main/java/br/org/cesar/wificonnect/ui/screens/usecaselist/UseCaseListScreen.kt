package br.org.cesar.wificonnect.ui.screens.usecaselist

import android.content.ComponentName
import android.content.ContentResolver
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.SettingsAccessibility
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import br.org.cesar.wificonnect.data.local.mapper.UseCaseRouteMap
import br.org.cesar.wificonnect.data.service.PocAccessibilityService
import br.org.cesar.wificonnect.ui.components.tiles.instagram.ScrollReelsTileRoot
import br.org.cesar.wificonnect.ui.components.tiles.network.mobilesignal.NetworkSignalTileRoot
import br.org.cesar.wificonnect.ui.components.tiles.network.wifirequest.NetworkRequestTileRoot
import br.org.cesar.wificonnect.ui.components.tiles.playstore.InstallAppTileRoot
import br.org.cesar.wificonnect.ui.components.tiles.system.RunAppTileRoot
import br.org.cesar.wificonnect.ui.components.tiles.wechat.WeChatTileRoot
import br.org.cesar.wificonnect.ui.navigation.NavManager
import br.org.cesar.wificonnect.ui.theme.DesignSystemTheme

@Composable
fun UseCaseListScreenRoot(
    navManager: NavManager,
    routes: UseCaseRouteMap = UseCaseRouteMap(),
) {
    val viewModel = hiltViewModel<UseCaseListViewModel>()
    val uiState by viewModel.uiState.collectAsState()

    UseCaseListScreen(
        uiState = uiState,
        routes = routes,
        onA11yStateUpdate = { viewModel.updateA11yState(it) },
        onA11yComponentNameUpdate = { viewModel.updateUiState(a11yServiceComponentName = it) },
        onNavigateUp = navManager::navigateUp
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UseCaseListScreen(
    uiState: UseCaseListUiState,
    routes: UseCaseRouteMap,
    onA11yStateUpdate: (ContentResolver) -> Unit,
    onA11yComponentNameUpdate: (ComponentName?) -> Unit,
    onNavigateUp: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()
    val permissions = mutableListOf<String>()

    onA11yComponentNameUpdate(
        ComponentName(context, PocAccessibilityService::class.java)
    )

    val callbackA11yStateCheck: () -> Boolean? = onA11yStateCheck@{
        onA11yStateUpdate(context.contentResolver)
        if (uiState.isA11yEnabled != true) {
            context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }

        return@onA11yStateCheck uiState.isA11yEnabled
    }

    LaunchedEffect(lifecycleState) {
        when (lifecycleState) {
            Lifecycle.State.RESUMED -> {
                onA11yStateUpdate(context.contentResolver)
            }

            else -> {}
        }
    }

    val tiles: List<@Composable () -> Unit> = listOf(
        { NetworkRequestTileRoot(callbackA11yStateCheck, routes.networkRequestRoute, { permissions.addAll(it) }) },
        { InstallAppTileRoot(callbackA11yStateCheck, routes.playStoreInstallRoute) },
        { NetworkSignalTileRoot({ permissions.addAll(it) }) },
        { RunAppTileRoot() },
        { ScrollReelsTileRoot(callbackA11yStateCheck) },
        { WeChatTileRoot(callbackA11yStateCheck) },
    )

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
                title = { Text("Use Cases") },
                actions = { UseCaseListActions(uiState) }
            )
        }
    ) { innerPadding ->
        Box(
            Modifier.padding(innerPadding)
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                tiles.forEachIndexed { index, tileComposable ->
                    tileComposable()
                    if (index < tiles.size - 1) {
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
private fun UseCaseListActions(
    uiState: UseCaseListUiState,
) {
    val context = LocalContext.current

    IconButton(
        onClick = {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            context.startActivity(intent)
        }
    ) {
        Box {
            Icon(
                imageVector = Icons.Default.SettingsAccessibility,
                contentDescription = null,
                tint = if (uiState.isA11yEnabled == true) {
                    Color.Green
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
            )

            /*
            if (uiState.isA11yEnabled == true) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    tint = Color.Green.copy(alpha = 0.8f),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(24.dp)
                )
            }
             */
        }
    }
}

@Preview
@Composable
private fun UseCaseListScreenPreview() {
    DesignSystemTheme {
        UseCaseListScreen(
            uiState = UseCaseListUiState(),
            routes = UseCaseRouteMap(),
            onA11yStateUpdate = {},
            onA11yComponentNameUpdate = {},
            onNavigateUp = {}
        )
    }
}