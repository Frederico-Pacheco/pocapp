package br.org.cesar.wificonnect.ui.components.tiles.instagram

import android.content.Intent
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.net.toUri
import br.org.cesar.wificonnect.ui.components.PrefsItem
import br.org.cesar.wificonnect.ui.components.RunIconButton
import br.org.cesar.wificonnect.ui.theme.DesignSystemTheme

@Composable
fun ScrollReelsTileRoot(
    onA11yStateCheck: () -> Boolean?,
) {
    ScrollReelsTile(
        uiState = ScrollReelsUiState(),
        onA11yStateCheck = onA11yStateCheck
    )
}

@Composable
private fun ScrollReelsTile(
    uiState: ScrollReelsUiState,
    onA11yStateCheck: () -> Boolean?,
) {
    val context = LocalContext.current

    PrefsItem(
        icon = { },
        text = { Text("List Instagram reels") },
        secondaryText = { Text("") },
        trailing = {
            RunIconButton(
                isRunning = false,
                onClick = {
                    val isEnabled = onA11yStateCheck()
                    if (isEnabled == true) {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            uiState.deepLink.toUri()
                        )
                        intent.setPackage(uiState.packageName)
                        context.startActivity(intent)
                    }
                }
            )
        }
    )
}

@Preview
@Composable
private fun ScrollReelsTilePreview() {
    DesignSystemTheme {
        Surface {
            ScrollReelsTile(
                uiState = ScrollReelsUiState(),
                onA11yStateCheck = { false }
            )
        }
    }
}