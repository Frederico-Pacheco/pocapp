package br.org.cesar.wificonnect.ui.screens

import androidx.compose.foundation.clickable
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
import br.org.cesar.wificonnect.ui.components.PrefsItem
import br.org.cesar.wificonnect.ui.navigation.NavManager
import br.org.cesar.wificonnect.ui.theme.DesignSystemTheme

@Composable
fun MainScreenRoot(
    navManager: NavManager,
) {
    MainScreen(
        onNavigateToAllUseCases = { navManager.navigateToAllUseCases() },
        onNavigateToReRunApp = { navManager.navigateToReRunApp() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreen(
    onNavigateToAllUseCases: () -> Unit,
    onNavigateToReRunApp: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PoC App") },
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
                    text = {
                        Text(
                            text = "All use cases",
                            modifier = Modifier.clickable { onNavigateToAllUseCases() }
                        )
                    },
                )
                HorizontalDivider()

                PrefsItem(
                    text = {
                        Text(
                            text = "Install app and re-run PlayStore",
                            modifier = Modifier.clickable { onNavigateToReRunApp() }
                        )
                    },
                )
            }
        }
    }
}

@Preview
@Composable
private fun MainScreenPreview() {
    DesignSystemTheme {
        MainScreen(
            onNavigateToAllUseCases = {},
            onNavigateToReRunApp = {}
        )
    }
}