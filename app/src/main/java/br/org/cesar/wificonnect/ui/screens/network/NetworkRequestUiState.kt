package br.org.cesar.wificonnect.ui.screens.network

import android.content.pm.PackageManager
import br.org.cesar.wificonnect.domain.usecase.UseCaseListener
import br.org.cesar.wificonnect.domain.usecase.UseCaseStatus
import java.text.DecimalFormat

data class NetworkRequestUiState(
    val wifiSsid: String? = null,
    val wifiPsk: String? = null,
    val isWifiEnabled: Boolean = false,
    val requestDuration: Long? = null,
    val useCaseListener: UseCaseListener? = null,
    val listenerMessage: String = "",
    val useCaseStatus: UseCaseStatus = UseCaseStatus.NOT_EXECUTED,
    val permissionStatus: Int = PackageManager.PERMISSION_DENIED,
    val isLoading: Boolean = false
) {
    fun getFormattedRequestDuration(): String {
        val formatter = DecimalFormat("#.###")
        return requestDuration?.let {
            "Duration: ${formatter.format(it)} ms"
        } ?: ""
    }
}