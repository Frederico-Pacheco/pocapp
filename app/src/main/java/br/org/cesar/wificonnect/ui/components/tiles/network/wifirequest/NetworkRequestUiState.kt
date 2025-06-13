package br.org.cesar.wificonnect.ui.components.tiles.network.wifirequest

import android.content.pm.PackageManager
import br.org.cesar.wificonnect.domain.usecase.UseCaseStatus
import java.text.DecimalFormat

data class NetworkRequestUiState(
    val wifiSsid: String? = null,
    val wifiPsk: String? = null,
    val isWifiEnabled: Boolean = false,
    val requestDurations: List<Long?> = listOf(),
    val listenerMessage: String = "",
    val useCaseStatus: UseCaseStatus = UseCaseStatus.NOT_EXECUTED,
    val permissionStatus: Int = PackageManager.PERMISSION_DENIED,
    val isRunning: Boolean = false,
) {
    fun getFormattedRequestDuration(): String {
        val formatter = DecimalFormat("#.###")
        formatter.groupingSize = 3
        formatter.isGroupingUsed = true

        if (requestDurations.size > 1) {
            return "1st: ${formatter.format(requestDurations[0])} ms;\t" +
                    "2nd: ${formatter.format(requestDurations[1])} ms"
        }

        return ""
    }
}