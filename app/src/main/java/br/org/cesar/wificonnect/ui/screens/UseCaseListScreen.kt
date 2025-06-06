package br.org.cesar.wificonnect.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.org.cesar.wificonnect.data.local.UseCaseRouteMap
import br.org.cesar.wificonnect.ui.navigation.NavManager
import br.org.cesar.wificonnect.ui.screens.tiles.network.NetworkRequestTileRoot
import br.org.cesar.wificonnect.ui.screens.tiles.playstore.InstallAppTileRoot
import br.org.cesar.wificonnect.ui.theme.DesignSystemTheme

@Composable
fun UseCaseListScreenRoot(
    navManager: NavManager,
    routes: UseCaseRouteMap = UseCaseRouteMap(),
) {
    UseCaseListScreen(
        routes = routes
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UseCaseListScreen(
    routes: UseCaseRouteMap,
) {
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
                NetworkRequestTileRoot(routes.networkRequestRoute)
                HorizontalDivider()

                InstallAppTileRoot()
            }
        }
    }
}

@Preview
@Composable
private fun UseCaseListScreenPreview() {
    DesignSystemTheme {
        UseCaseListScreen(
            routes = UseCaseRouteMap()
        )
    }
}