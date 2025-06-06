package br.org.cesar.wificonnect.data.local

import br.org.cesar.wificonnect.ui.navigation.AppNavDestination

data class UseCaseRouteMap(
    var networkRequestRoute: AppNavDestination.NetworkRequest = AppNavDestination.NetworkRequest()
)