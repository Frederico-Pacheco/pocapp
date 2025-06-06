package br.org.cesar.wificonnect.domain.usecase.playstore

import br.org.cesar.wificonnect.domain.usecase.UseCaseStatus

data class InstallAppUseCaseState(
    val durationMillis: Long? = null,
    val useCaseStatus: UseCaseStatus = UseCaseStatus.NOT_EXECUTED,
)