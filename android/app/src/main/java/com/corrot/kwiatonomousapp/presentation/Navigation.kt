package com.corrot.kwiatonomousapp.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.corrot.kwiatonomousapp.common.Constants.NAV_ARG_DEVICE_ID
import com.corrot.kwiatonomousapp.presentation.dasboard.DashboardScreen
import com.corrot.kwiatonomousapp.presentation.devices.DevicesScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.Devices.route) {

        composable(route = Screen.Devices.route) {
            DevicesScreen(navController)
        }

        composable(
            route = Screen.Dashboard.route + "/{$NAV_ARG_DEVICE_ID}",
            arguments = listOf(navArgument(name = NAV_ARG_DEVICE_ID) {
                type = NavType.StringType
                nullable = false
            })
        ) {
            DashboardScreen()
        }

    }
}

sealed class Screen(val route: String) {
    object Dashboard : Screen("/dashboard")
    object Devices : Screen("/devices")

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { append("/$it") }
        }
    }
}