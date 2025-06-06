package br.org.cesar.wificonnect.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import br.org.cesar.wificonnect.data.local.UseCaseRouteMap
import br.org.cesar.wificonnect.ui.screens.UseCaseListScreenRoot

fun NavGraphBuilder.appNavGraph(
    navManager: NavManager,
) {
    navigation<AppNavDestination.Root>(
        startDestination = AppNavDestination.UseCaseList
    ) {
        composable<AppNavDestination.UseCaseList> {
            UseCaseListScreenRoot(navManager)
        }

        composable<AppNavDestination.NetworkRequest> { backStackEntry ->
            val route = backStackEntry.toRoute<AppNavDestination.NetworkRequest>()

            UseCaseListScreenRoot(
                navManager = navManager,
                routes = UseCaseRouteMap(networkRequestRoute = route)
            )
        }
    }
}