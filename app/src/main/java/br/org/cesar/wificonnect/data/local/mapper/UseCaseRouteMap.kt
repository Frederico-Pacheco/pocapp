package br.org.cesar.wificonnect.data.local.mapper

import br.org.cesar.wificonnect.ui.navigation.AppNavDestination

data class UseCaseRouteMap(
    var networkRequestRoute: AppNavDestination.NetworkRequest = AppNavDestination.NetworkRequest(),
    var playStoreInstallRoute: AppNavDestination.PlayStoreInstall = AppNavDestination.PlayStoreInstall()
)