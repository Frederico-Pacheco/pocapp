package br.org.cesar.wificonnect.domain.usecase.wechat

import android.accessibilityservice.GestureDescription
import android.content.Context
import android.graphics.Path
import android.util.Log
import android.view.WindowManager
import br.org.cesar.wificonnect.data.local.model.Device
import br.org.cesar.wificonnect.domain.usecase.OverlayUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeChatUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val overlayUseCase: OverlayUseCase,
    private val mWindowManager: WindowManager,
) {
    var press = 0
    var isFoldable = false

    private var device: Device? = null

    fun tapOnFirstChat(): GestureDescription {
        // positionX: 540.0 positionY: 351.0
        val (ratioX, ratioY) = if (getDeviceInfo().isOpenFoldable()) {
            0.921f to 0.909f // 92.1% of screen width and 90.9% of screen height
        } else {
            0.50f to 0.15f // 50.0% of screen width and 15.0% of screen height
        }

        val positionX = getDeviceInfo().width * ratioX
        val positionY = getDeviceInfo().height * ratioY

        return tapAt(positionX, positionY)
    }

    fun tapOnMoreButton(): GestureDescription {
        // positionX: 982.0 positionY: 2106.0
        val positionX = getDeviceInfo().width * 0.91f
        val positionY = getDeviceInfo().height * 0.9f

        return tapAt(positionX, positionY)
    }

    fun tapOnVideoCall(): GestureDescription {
        // positionX: 648.0 positionY: 1521.0
        val positionX = getDeviceInfo().width * 0.6f
        val positionY = getDeviceInfo().height * 0.65f

        return tapAt(positionX, positionY)
    }

    private fun getDeviceInfo(): Device {
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

    private fun tapAt(x: Float, y: Float, duration: Long = 50L): GestureDescription {
        val path = Path().apply { moveTo(x, y) }

        Log.d(TAG, "Tap position -> x: $x y: $y")
        overlayUseCase.showClickIndicator(x, y)

        return GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, duration))
            .build()
    }

    companion object {
        private val TAG = WeChatUseCase::class.java.simpleName
    }
}