package br.org.cesar.wificonnect.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import br.org.cesar.wificonnect.ui.screens.network.NetworkRequestScreenRoot

fun NavGraphBuilder.appNavGraph(
    navManager: NavManager,
) {
    navigation<AppNavDestination.Root>(
        startDestination = AppNavDestination.NetworkRequest()
    ) {
        composable<AppNavDestination.NetworkRequest> { backStackEntry ->
            val route = backStackEntry.toRoute<AppNavDestination.NetworkRequest>()

            NetworkRequestScreenRoot(
                navManager = navManager,
                navRoute = route
            )
        }
    }
}