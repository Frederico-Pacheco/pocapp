package br.org.cesar.wificonnect.data.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import br.org.cesar.wificonnect.domain.usecase.instagram.ScrollReelsUseCase
import br.org.cesar.wificonnect.domain.usecase.playstore.InstallAppUseCase
import br.org.cesar.wificonnect.domain.usecase.system.RunAppUseCase
import br.org.cesar.wificonnect.domain.usecase.wechat.WeChatQrCodeUseCase
import br.org.cesar.wificonnect.domain.usecase.wechat.WeChatUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PocAccessibilityService : AccessibilityService() {
    @Inject
    lateinit var mInstallAppUseCase: InstallAppUseCase

    @Inject
    lateinit var mRunAppUseCase: RunAppUseCase

    @Inject
    lateinit var mScrollReelsUseCase: ScrollReelsUseCase

    @Inject
    lateinit var mWeChatUseCase: WeChatUseCase

    @Inject
    lateinit var mWeChatQrCodeUseCase: WeChatQrCodeUseCase


    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        val packageName = event.packageName?.toString()
        mRunAppUseCase.addAppPackageName(packageName)
        when (packageName) {
            "com.android.settings" -> startEvent(event, ::handleSettingsUiPopup)

            "com.android.vending" -> startEvent(event, ::handlePlayStoreUi)

            "com.instagram.android" -> startEvent(event, ::handleInstagramUi)

            "com.tencent.mm" -> startEvent(event, ::handleWeChatQrCodeUi)
        }
    }

    override fun onInterrupt() {
        // TODO("Not yet implemented")
    }

    private fun startEvent(event: AccessibilityEvent, onEvent: (AccessibilityNodeInfo?) -> Unit) {
        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED,
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
                onEvent(rootInActiveWindow)
            }

            else -> {}
        }
    }

    private fun handleSettingsUiPopup(rootNode: AccessibilityNodeInfo?) {
        if (rootNode == null) return

        val nodes = rootNode.findAccessibilityNodeInfosByText("Conectar")
        if (nodes.isNullOrEmpty()) {
            return
        }

        nodes[0].performAction(AccessibilityNodeInfo.ACTION_CLICK)
    }

    private fun handlePlayStoreUi(rootNode: AccessibilityNodeInfo?) {
        mInstallAppUseCase.handlePlayStoreUi(rootNode) {
            performGlobalAction(GLOBAL_ACTION_BACK)
        }
    }

    private fun handleInstagramUi(rootNode: AccessibilityNodeInfo?) {
        mScrollReelsUseCase.handleInstagramUi(rootNode) {
            performGlobalAction(GLOBAL_ACTION_BACK)
        }
    }

    private fun handleWeChatUi(rootNode: AccessibilityNodeInfo?) {
        if (mWeChatUseCase.press == 0) {
            mWeChatUseCase.press = 1

            CoroutineScope(Dispatchers.Main).launch {
                delay(2000)
                doGesture(mWeChatUseCase.tapOnFirstChat())

                delay(5000)
                doGesture(mWeChatUseCase.tapOnMoreButton())

                delay(5000)
                doGesture(mWeChatUseCase.tapOnVideoCall())
            }
        }

    }

    private fun handleWeChatQrCodeUi(rootNode: AccessibilityNodeInfo?) {
        if (mWeChatQrCodeUseCase.press == 0) {
            mWeChatQrCodeUseCase.press = 1

            CoroutineScope(Dispatchers.Main).launch {
                delay(2000)
                doGesture(mWeChatQrCodeUseCase.tapOnDiscoverButton())

                delay(5000)
                doGesture(mWeChatQrCodeUseCase.tapOnScanButton())

                delay(5000)
                doGesture(mWeChatQrCodeUseCase.tapOnAlbumButton())

                delay(5000)
                doGesture(mWeChatQrCodeUseCase.tapOnThirdAlbumItem())
            }
        }
    }

    private fun doGesture(gesture: GestureDescription) {
        dispatchGesture(gesture, object : GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription) {
                super.onCompleted(gestureDescription)
                Log.d(TAG, "Gesture completed")
            }

            override fun onCancelled(gestureDescription: GestureDescription) {
                super.onCancelled(gestureDescription)
                Log.d(TAG, "Gesture cancelled")
            }
        }, null)
    }

    companion object {
        private val TAG = PocAccessibilityService::class.java.simpleName
    }
}