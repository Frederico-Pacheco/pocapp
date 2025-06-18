package br.org.cesar.wificonnect.ui.navigation

import androidx.navigation.NavHostController

class NavManager(
    private val navHostController: NavHostController,
) {
    fun navigateToAllUseCases() {
        navHostController.navigate(AppNavDestination.UseCaseList)
    }

    fun navigateToReRunApp() {
        navHostController.navigate(AppNavDestination.ReRunApp)
    }

    fun navigateUp() {
        navHostController.navigateUp()
    }
}