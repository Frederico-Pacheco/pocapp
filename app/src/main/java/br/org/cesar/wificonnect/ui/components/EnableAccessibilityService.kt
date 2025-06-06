package br.org.cesar.wificonnect.ui.components

import android.content.ComponentName
import android.content.Intent
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import br.org.cesar.wificonnect.service.PocAccessibilityService

@Composable
fun EnableAccessibilityService(
    isAccessibilityServiceEnabled: Boolean?,
    onVerify: (String?, ComponentName) -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(isAccessibilityServiceEnabled) {
        if (isAccessibilityServiceEnabled == null) {
            val expectedComponentName = ComponentName(context, PocAccessibilityService::class.java)
            val enabledServicesSetting = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )

            onVerify(enabledServicesSetting, expectedComponentName)
        } else if (!isAccessibilityServiceEnabled) {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            context.startActivity(intent)
        }
    }
}