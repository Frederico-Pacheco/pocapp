package br.org.cesar.wificonnect.ui.screens.tiles.playstore

import br.org.cesar.wificonnect.domain.usecase.UseCaseStatus
import java.text.DecimalFormat

data class InstallAppUiState(
    val companyName: String? = null,
    val packageName: String? = null,
    val durationMillis: Long? = null,
    val timeoutMillis: Long = 10000L,
    val listenerMessage: String = "",
    val useCaseStatus: UseCaseStatus = UseCaseStatus.NOT_EXECUTED,
    val isRunning: Boolean = false,
) {
    fun getFormattedRequestDuration(): String {
        val formatter = DecimalFormat("#.###")
        formatter.groupingSize = 3
        formatter.isGroupingUsed = true

        return "1st: ${formatter.format(durationMillis)} ms"
    }
}
