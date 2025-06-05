package br.org.cesar.wificonnect.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost

@Composable
fun AppNavHost(
    navHostController: NavHostController,
) {
    NavHost(
        navController = navHostController,
        startDestination = AppNavDestination.Root
    ) {
        val navManager = NavManager(navHostController)

        appNavGraph(navManager)
    }
}