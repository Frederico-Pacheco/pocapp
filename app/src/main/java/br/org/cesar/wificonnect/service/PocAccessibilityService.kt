package br.org.cesar.wificonnect.service

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import br.org.cesar.wificonnect.domain.usecase.playstore.InstallAppUseCase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PocAccessibilityService : AccessibilityService() {
    @Inject
    lateinit var useCase: InstallAppUseCase

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        val packageName = event.packageName?.toString()
        when (packageName) {
            "com.android.settings" -> startEvent(event, ::handleSettingsUiPopup)

            "com.android.vending" -> startEvent(event, ::handlePlayStoreUi)
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
        if (rootNode == null) return
        val nodes = mutableListOf<AccessibilityNodeInfo>()

        fun findAllButtons(node: AccessibilityNodeInfo?) {
            if (node == null) return

            if (node.isClickable && node.isVisibleToUser) {
                nodes.add(node)
            }

            for (i in 0 until node.childCount) {
                findAllButtons(node.getChild(i))
            }
        }

        findAllButtons(rootNode)

        if (nodes.size > 3
            && nodes[0].text != null
            && nodes[0].text.toString() == useCase.companyName
            && !useCase.wasRequested(packageManager)
            && useCase.getInstallDuration() == null
        ) {
            val node = nodes[3]
            node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            useCase.setRequestTime(System.currentTimeMillis())
            useCase.onInstall(packageManager) {
                performGlobalAction(GLOBAL_ACTION_BACK)
            }
        }
    }

    companion object {
        private val TAG = PocAccessibilityService::class.java.simpleName
    }
}