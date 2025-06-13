package br.org.cesar.wificonnect.ui.screens.tiles.system

import br.org.cesar.wificonnect.domain.usecase.UseCaseStatus

data class RunAppUiState(
    val listenerMessage: String = "",
    val useCaseStatus: UseCaseStatus = UseCaseStatus.NOT_EXECUTED,
    val isRunning: Boolean = false,
)