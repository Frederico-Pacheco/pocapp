package br.org.cesar.wificonnect.domain.usecase.playstore

import android.content.pm.PackageManager
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
    private val mDispatcherProvider: DispatcherProvider
) {
    private val _state = MutableStateFlow(InstallAppUseCaseState())
    val state: StateFlow<InstallAppUseCaseState> = _state.asStateFlow()

    var companyName: String? = null
    var pkgName: String? = null
    var useCaseListener: UseCaseListener? = null
    var timeoutMillis: Long = 10000L

    private var mRequestTime: Long? = null

    fun onInstall(pkgMgr: PackageManager, callback: () -> Unit) {
        val startTime = System.currentTimeMillis()
        useCaseListener?.onUseCaseMsgReceived("Installing app: $pkgName")

        mRequestTime?.let { requestTime ->
            CoroutineScope(mDispatcherProvider.main).launch {
                val endTime = System.currentTimeMillis()
                while (endTime - startTime < timeoutMillis) {
                    if (isInstalled(pkgMgr)) {
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

    fun wasRequested(pkgMgr: PackageManager): Boolean {
        return isInstalled(pkgMgr) && mRequestTime != null
    }

    fun setRequestTime(startTime: Long) {
        mRequestTime = startTime
        useCaseListener?.onUseCaseMsgReceived("Requesting app installation...")
    }

    fun getInstallDuration(): Long? {
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

    private fun isInstalled(pkgMgr: PackageManager): Boolean {
        return try {
            pkgMgr.getPackageInfo(pkgName!!, 0)

            useCaseListener?.onUseCaseSuccess()
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
}