package br.org.cesar.wificonnect.domain.usecase.accessibility

import android.content.ComponentName
import android.content.ContentResolver
import android.provider.Settings

class AccessibilityServiceUseCase {
    companion object {
        fun isServiceEnabled(
            resolver: ContentResolver,
            expectedComponentName: ComponentName
        ): Boolean? {
            var isEnabled: Boolean? = null

            val enabledServicesSetting = Settings.Secure.getString(
                resolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )

            enabledServicesSetting?.let { serviceName ->
                val componentName = ComponentName.unflattenFromString(serviceName)
                isEnabled = (componentName != null && componentName == expectedComponentName)
            }

            return isEnabled
        }
    }
}