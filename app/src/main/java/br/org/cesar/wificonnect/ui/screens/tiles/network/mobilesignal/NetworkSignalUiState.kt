package br.org.cesar.wificonnect.ui.screens.tiles.network.mobilesignal

import android.content.pm.PackageManager
import br.org.cesar.wificonnect.domain.usecase.UseCaseStatus

data class NetworkSignalUiState(
    val isRunning: Boolean = false,
    val useCaseStatus: UseCaseStatus = UseCaseStatus.NOT_EXECUTED,
    val permissionStatus: Int = PackageManager.PERMISSION_DENIED,
)
