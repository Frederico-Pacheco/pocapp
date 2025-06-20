package br.org.cesar.wificonnect.domain.usecase.instagram

import android.view.accessibility.AccessibilityNodeInfo
import br.org.cesar.wificonnect.common.dispatcher.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

private var scrollJob: Job? = null

@Singleton
class ScrollReelsUseCase @Inject constructor(
    private val mDispatcherProvider: DispatcherProvider
) {
    private val maxIterations = 38

    private var hasClickedOpenReel = false
    private var currentIteration = 0

    fun handleInstagramUi(
        rootNode: AccessibilityNodeInfo?,
        performBackClick: () -> Unit
    ) {
        if (rootNode == null) return

        if (!hasClickedOpenReel) {
            findReelNode(rootNode)?.apply {
                performAction(AccessibilityNodeInfo.ACTION_CLICK)
                hasClickedOpenReel = true
            }
        } else {
            findScrollableNode(rootNode)?.let { node ->
                scrollReel(performBackClick, node)
            }
        }
    }

    private fun scrollReel(performBackClick: () -> Unit, node: AccessibilityNodeInfo) {
        if (scrollJob?.isActive != true) {
            scrollJob = CoroutineScope(mDispatcherProvider.main).launch {
                currentIteration += 1
                if (currentIteration > maxIterations) {
                    currentIteration = 0
                    performBackClick()
                } else {
                    delay(5000)
                    node.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)
                }
            }
        }
    }

    private fun findReelNode(node: AccessibilityNodeInfo?): AccessibilityNodeInfo? {
        val onCheckConditions: (AccessibilityNodeInfo) -> Boolean = { nodeInfo ->
            nodeInfo.isClickable && nodeInfo.isVisibleToUser
                    && nodeInfo.className == "android.widget.ImageView"
                    && nodeInfo.contentDescription.contains("Reel de appletv")
        }

        return findNode(node, onCheckConditions)
    }

    private fun findScrollableNode(node: AccessibilityNodeInfo?): AccessibilityNodeInfo? {
        val onCheckConditions: (AccessibilityNodeInfo) -> Boolean = { nodeInfo ->
            nodeInfo.isScrollable && nodeInfo.isVisibleToUser
                    && nodeInfo.className == "androidx.viewpager.widget.ViewPager"
        }

        return findNode(node, onCheckConditions)
    }

    private fun findNode(
        node: AccessibilityNodeInfo?,
        checkConditions: (AccessibilityNodeInfo) -> Boolean
    ): AccessibilityNodeInfo? {
        if (node == null) return null

        if (checkConditions(node)) {
            return node
        }

        for (i in 0 until node.childCount) {
            val foundNode = findNode(node.getChild(i), checkConditions)
            if (foundNode != null) {
                return foundNode
            }
        }

        return null
    }
}