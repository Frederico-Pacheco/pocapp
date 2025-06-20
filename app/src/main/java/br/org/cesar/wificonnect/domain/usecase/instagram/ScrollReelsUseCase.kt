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
    private var currentIteration = 0

    fun handleInstagramUi(
        rootNode: AccessibilityNodeInfo?,
        performBackClick: () -> Unit
    ) {
        if (rootNode == null) return
        var scrollableNode: AccessibilityNodeInfo? = null

        fun findScrollableNode(node: AccessibilityNodeInfo?) {
            if (node == null) return

            if (node.isScrollable && node.isVisibleToUser) {
                scrollableNode = node
            }

            for (i in 0 until node.childCount) {
                findScrollableNode(node.getChild(i))
                if (scrollableNode != null) {
                    break
                }
            }
        }

        findScrollableNode(rootNode)

        if (scrollableNode != null
            && scrollableNode?.className == "androidx.viewpager.widget.ViewPager"
        ) {
            scrollableNode?.let { node ->
                if (node.isScrollable && scrollJob?.isActive != true) {
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
        }
    }
}