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
fun ListReelsTileRoot(
    onA11yStateCheck: () -> Boolean?,
) {
    ListReelsTile(
        onA11yStateCheck = onA11yStateCheck
    )
}

@Composable
private fun ListReelsTile(
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
                            "instagram://user?username=natgeo".toUri()
                        )
                        intent.setPackage("com.instagram.android")
                        context.startActivity(intent)
                    }
                }
            )
        }
    )
}

@Preview
@Composable
private fun ListReelsTilePreview() {
    DesignSystemTheme {
        Surface {
            ListReelsTile(
                onA11yStateCheck = { false }
            )
        }
    }
}