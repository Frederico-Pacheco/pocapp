package br.org.cesar.wificonnect.domain.usecase.accessibility

import android.content.ComponentName

class AccessibilityServiceUseCase {
    companion object {
        fun isAccessibilityServiceEnabled(
            serviceSetting: String?,
            expectedComponentName: ComponentName
        ): Boolean? {
            var isEnabled: Boolean? = null

            serviceSetting?.let { serviceName ->
                val componentName = ComponentName.unflattenFromString(serviceName)
                isEnabled = (componentName != null && componentName == expectedComponentName)
            }

            return isEnabled
        }
    }
}