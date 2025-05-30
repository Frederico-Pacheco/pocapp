package br.org.cesar.wificonnect.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import br.org.cesar.wificonnect.ui.screens.network.NetworkRequestScreenRoot
import br.org.cesar.wificonnect.ui.theme.DesignSystemTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            DesignSystemTheme {
                NetworkRequestScreenRoot()
            }
        }
    }
}
