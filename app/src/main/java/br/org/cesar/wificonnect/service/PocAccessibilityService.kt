package br.org.cesar.wificonnect.service

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class PocAccessibilityService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        val packageName = event.packageName?.toString()
        if (packageName == "com.android.settings") {
            when (event.eventType) {
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED,
                AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
                    handleSystemUiPopup(rootInActiveWindow)
                }

                else -> {}
            }
        }
    }

    override fun onInterrupt() {
        TODO("Not yet implemented")
    }

    private fun handleSystemUiPopup(rootNode: AccessibilityNodeInfo?) {
        if (rootNode == null) return

        val nodes = rootNode.findAccessibilityNodeInfosByText("Conectar")
        if (nodes.isNullOrEmpty()) {
            Log.e(TAG, "'Connect' button not found")
            return
        }

        nodes[0].performAction(AccessibilityNodeInfo.ACTION_CLICK)
    }

    companion object {
        private val TAG = PocAccessibilityService::class.java.simpleName
    }
}