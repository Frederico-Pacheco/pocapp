package br.org.cesar.wificonnect.domain.usecase.playstore

import android.content.Context
import android.content.pm.PackageManager
import br.org.cesar.wificonnect.common.dispatcher.DispatcherProvider
import br.org.cesar.wificonnect.domain.usecase.UseCaseListener
import br.org.cesar.wificonnect.domain.usecase.UseCaseStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

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

    fun onInstall(context: Context, callback: () -> Unit) {
        val startTime = System.currentTimeMillis()

        mRequestTime?.let { requestTime ->
            CoroutineScope(mDispatcherProvider.main).launch {
                val endTime = System.currentTimeMillis()
                while (endTime - startTime < timeoutMillis) {
                    if (isInstalled(context)) {
                        updateState(
                            durationMillis = endTime - requestTime,
                            useCaseStatus = UseCaseStatus.SUCCESS
                        )
                        callback()
                        return@launch
                    }
                    delay(500L)
                }
                updateState(useCaseStatus = UseCaseStatus.ERROR)
                callback()
            }
        }
    }

    fun wasRequested(context: Context): Boolean {
        return isInstalled(context) && mRequestTime != null
    }

    fun setRequestTime(startTime: Long) {
        mRequestTime = startTime
    }

    fun getInstallDuration(): Long? {
        return state.value.durationMillis
    }

    fun setUseCaseStatus(useCaseStatus: UseCaseStatus) {
        updateState(useCaseStatus = useCaseStatus)
    }

    private fun updateState(
        durationMillis: Long? = null,
        useCaseStatus: UseCaseStatus? = null
    ) {
        _state.update { currentState ->
            currentState.copy(
                durationMillis = durationMillis ?: currentState.durationMillis,
                useCaseStatus = useCaseStatus ?: currentState.useCaseStatus
            )
        }
    }

    private fun isInstalled(context: Context): Boolean {
        return try {
            context.packageManager.getPackageInfo(pkgName!!, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
}