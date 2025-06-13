package br.org.cesar.wificonnect.domain.usecase.playstore

import android.content.pm.PackageManager
import android.view.accessibility.AccessibilityNodeInfo
import br.org.cesar.wificonnect.common.dispatcher.DispatcherProvider
import br.org.cesar.wificonnect.domain.usecase.UseCaseListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InstallAppUseCase @Inject constructor(
    private val mPackageManager: PackageManager,
    private val mDispatcherProvider: DispatcherProvider
) {
    private val _state = MutableStateFlow(InstallAppUseCaseState())
    val state: StateFlow<InstallAppUseCaseState> = _state.asStateFlow()

    var companyName: String? = null
    var pkgName: String? = null
    var useCaseListener: UseCaseListener? = null
    var timeoutMillis: Long = 10000L

    private var mRequestTime: Long? = null

    fun handlePlayStoreUi(
        rootNode: AccessibilityNodeInfo?,
        installCallback: () -> Unit
    ) {
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
            && nodes[0].text.toString() == companyName
            && !wasRequested()
            && getInstallDuration() == null
        ) {
            val node = nodes[3]
            node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            setRequestTime(System.currentTimeMillis())
            onInstall {
                installCallback()
            }
        }
    }

    private fun onInstall(callback: () -> Unit) {
        val startTime = System.currentTimeMillis()
        useCaseListener?.onUseCaseMsgReceived("Installing app: $pkgName")

        mRequestTime?.let { requestTime ->
            CoroutineScope(mDispatcherProvider.main).launch {
                val endTime = System.currentTimeMillis()
                while (endTime - startTime < timeoutMillis) {
                    if (isInstalled()) {
                        updateState(
                            durationMillis = endTime - requestTime,
                        )
                        callback()
                        return@launch
                    }
                    delay(200L)
                }

                useCaseListener?.onUseCaseFailed("App installation failed: Service timed out.")
                callback()
            }
        }
    }

    private fun wasRequested(): Boolean {
        return isInstalled() && mRequestTime != null
    }

    private fun setRequestTime(startTime: Long) {
        useCaseListener?.onUseCaseStarted()
        useCaseListener?.onUseCaseMsgReceived("Requesting app installation...")
        mRequestTime = startTime
    }

    private fun getInstallDuration(): Long? {
        return state.value.durationMillis
    }

    private fun updateState(
        durationMillis: Long? = null,
    ) {
        _state.update { currentState ->
            currentState.copy(
                durationMillis = durationMillis ?: currentState.durationMillis,
            )
        }
    }

    private fun isInstalled(): Boolean {
        return try {
            mPackageManager.getPackageInfo(pkgName!!, 0)

            useCaseListener?.onUseCaseSuccess()
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
}