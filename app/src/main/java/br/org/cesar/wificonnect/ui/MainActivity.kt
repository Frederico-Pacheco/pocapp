package br.org.cesar.wificonnect.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import br.org.cesar.wificonnect.ui.navigation.AppNavDestination
import br.org.cesar.wificonnect.ui.navigation.AppNavHost
import br.org.cesar.wificonnect.ui.theme.DesignSystemTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var navController: NavHostController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            navController = rememberNavController()

            DesignSystemTheme {
                AppNavHost(
                    navHostController = navController!!
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        when (intent.action) {
            "network.request" -> navigateToNetworkRequest(
                ssid = intent.getStringExtra("WIFI_SSID"),
                psk = intent.getStringExtra("WIFI_PSK")
            )
        }
    }

    private fun navigateToNetworkRequest(ssid: String?, psk: String?) {
        if (ssid != null && psk != null) {
            navController?.navigate(AppNavDestination.NetworkRequest(true, ssid, psk)) {
                launchSingleTop = true
            }
        }
    }
}
