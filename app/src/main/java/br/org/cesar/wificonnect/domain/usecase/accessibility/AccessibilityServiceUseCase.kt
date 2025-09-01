package br.org.cesar.wificonnect.domain.usecase.accessibility

import android.accessibilityservice.GestureDescription
import android.content.ComponentName
import android.content.ContentResolver
import android.content.Context
import android.graphics.Path
import android.provider.Settings
import android.util.Log
import android.view.WindowManager
import android.view.accessibility.AccessibilityNodeInfo
import br.org.cesar.wificonnect.data.local.model.Device
import br.org.cesar.wificonnect.domain.usecase.OverlayUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AccessibilityServiceUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val overlayUseCase: OverlayUseCase,
    private val mWindowManager: WindowManager,
) {
    var isFoldable = false
    private var device: Device? = null

    companion object {
        private val TAG = AccessibilityServiceUseCase::class.java.simpleName

        fun isServiceEnabled(
            resolver: ContentResolver,
            expectedComponentName: ComponentName
        ): Boolean? {
            var isEnabled: Boolean? = null

            val enabledServicesSettings = Settings.Secure.getString(
                resolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )?.split(":") ?: listOf()

            for (serviceName in enabledServicesSettings) {
                val componentName = ComponentName.unflattenFromString(serviceName)
                isEnabled = (componentName != null && componentName == expectedComponentName)

                if (isEnabled == true) {
                    break
                }
            }

            return isEnabled
        }
    }

    fun findNode(
        node: AccessibilityNodeInfo?,
        checkConditions: (AccessibilityNodeInfo) -> Boolean
    ): AccessibilityNodeInfo? {
        if (node == null) return null
        if (checkConditions(node)) return node

        for (i in 0 until node.childCount) {
            findNode(node.getChild(i), checkConditions)?.let {
                return it
            }
        }

        return null
    }

    fun tapAt(x: Float, y: Float, duration: Long = 50L): GestureDescription {
        val path = Path().apply { moveTo(x, y) }

        Log.d(TAG, "Tap position -> x: $x y: $y")
        overlayUseCase.showClickIndicator(x, y)
        overlayUseCase.showGridOverlay()

        return GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, duration))
            .build()
    }

    fun getDeviceInfo(): Device {
        if (device == null) {
            val bounds = mWindowManager.currentWindowMetrics.bounds
            val orientation = context.resources.configuration.orientation

            var fullWidth = bounds.width()
            var fullHeight = bounds.height()

            val internalDevice = Device(fullHeight, fullWidth, orientation, isFoldable)
            if (isFoldable && !internalDevice.isOpenFoldable() && fullHeight > fullWidth) {
                fullHeight = fullWidth.also { fullWidth = fullHeight }
            }

            device = Device(fullHeight, fullWidth, orientation, isFoldable)
            Log.d(
                TAG, "Current WindowMetrics - " +
                        "width: ${device?.width} px, height: ${device?.height} px"
            )
        }

        return device as Device
    }
}