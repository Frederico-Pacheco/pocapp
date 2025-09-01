package br.org.cesar.wificonnect.domain.usecase.wechat

import android.accessibilityservice.GestureDescription
import br.org.cesar.wificonnect.domain.usecase.accessibility.AccessibilityServiceUseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeChatQrCodeUseCase @Inject constructor(
    private val a11yServiceUseCase: AccessibilityServiceUseCase,
) {
    var press = 0
    var isFoldable = a11yServiceUseCase.isFoldable

    fun tapOnDiscoverButton(): GestureDescription {
        val (ratioX, ratioY) = if (a11yServiceUseCase.getDeviceInfo().isOpenFoldable()) {
            0.921f to 0.909f // 92.1% of screen width and 90.9% of screen height
        } else {
            0.570f to 0.920f // 57.0% of screen width and 92.0% of screen height
        }

        val positionX = a11yServiceUseCase.getDeviceInfo().width * ratioX
        val positionY = a11yServiceUseCase.getDeviceInfo().height * ratioY

        return a11yServiceUseCase.tapAt(positionX, positionY)
    }

    fun tapOnScanButton(): GestureDescription {
        val positionX = a11yServiceUseCase.getDeviceInfo().width * 0.50f
        val positionY = a11yServiceUseCase.getDeviceInfo().height * 0.18f

        return a11yServiceUseCase.tapAt(positionX, positionY)
    }

    fun tapOnAlbumButton(): GestureDescription {
        val positionX = a11yServiceUseCase.getDeviceInfo().width * 0.90f
        val positionY = a11yServiceUseCase.getDeviceInfo().height * 0.83f

        return a11yServiceUseCase.tapAt(positionX, positionY)
    }

    fun tapOnThirdAlbumItem(): GestureDescription {
        val positionX = a11yServiceUseCase.getDeviceInfo().width * 0.60f
        val positionY = a11yServiceUseCase.getDeviceInfo().height * 0.15f

        return a11yServiceUseCase.tapAt(positionX, positionY)
    }

    companion object {
        private val TAG = WeChatQrCodeUseCase::class.java.simpleName
    }
}