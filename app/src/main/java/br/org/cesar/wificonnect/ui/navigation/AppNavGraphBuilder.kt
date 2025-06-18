package br.org.cesar.wificonnect.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import br.org.cesar.wificonnect.data.local.UseCaseRouteMap
import br.org.cesar.wificonnect.ui.screens.MainScreenRoot
import br.org.cesar.wificonnect.ui.screens.rerunapp.ReRunAppScreenRoot
import br.org.cesar.wificonnect.ui.screens.usecaselist.UseCaseListScreenRoot

fun NavGraphBuilder.appNavGraph(
    navManager: NavManager,
) {
    navigation<AppNavDestination.Root>(
        startDestination = AppNavDestination.Main
    ) {
        composable<AppNavDestination.Main> {
            MainScreenRoot(navManager)
        }

        composable<AppNavDestination.UseCaseList> {
            UseCaseListScreenRoot(navManager)
        }

        composable<AppNavDestination.ReRunApp> {
            ReRunAppScreenRoot(navManager)
        }

        composable<AppNavDestination.NetworkRequest> { backStackEntry ->
            val route = backStackEntry.toRoute<AppNavDestination.NetworkRequest>()

            UseCaseListScreenRoot(
                navManager = navManager,
                routes = UseCaseRouteMap(networkRequestRoute = route)
            )
        }

        composable<AppNavDestination.PlayStoreInstall> { backStackEntry ->
            val route = backStackEntry.toRoute<AppNavDestination.PlayStoreInstall>()

            UseCaseListScreenRoot(
                navManager = navManager,
                routes = UseCaseRouteMap(playStoreInstallRoute = route)
            )
        }
    }
}