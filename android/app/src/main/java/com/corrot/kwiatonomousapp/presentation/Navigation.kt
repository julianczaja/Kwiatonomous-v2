package com.corrot.kwiatonomousapp.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.corrot.kwiatonomousapp.common.Constants.NAV_ARG_DEVICE_ID
import com.corrot.kwiatonomousapp.presentation.dasboard.DashboardScreen
import com.corrot.kwiatonomousapp.presentation.device_details.DeviceDetailsScreen
import com.corrot.kwiatonomousapp.presentation.device_settings.DeviceSettingsScreen
import com.corrot.kwiatonomousapp.presentation.devices.DevicesScreen
import com.corrot.kwiatonomousapp.presentation.splashscreen.SplashScreen

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.Splash.route) {

        composable(route = Screen.Splash.route) {
            SplashScreen(navController)
        }

        composable(route = Screen.Devices.route) {
            DevicesScreen(navController)
        }

        composable(
            route = Screen.DeviceDetails.route + "/{$NAV_ARG_DEVICE_ID}",
            arguments = listOf(navArgument(name = NAV_ARG_DEVICE_ID) {
                type = NavType.StringType
                nullable = false
            })
        ) {
            DeviceDetailsScreen(navController = navController)
        }

        composable(
            route = Screen.Dashboard.route
        ) {
            DashboardScreen(navController)
        }

        composable(
            route = Screen.DeviceSettings.route + "/{$NAV_ARG_DEVICE_ID}",
            arguments = listOf(navArgument(name = NAV_ARG_DEVICE_ID) {
                type = NavType.StringType
                nullable = false
            })
        ) {
            DeviceSettingsScreen(navController)
        }

    }
}

sealed class Screen(val route: String) {
    object Splash : Screen("/splash")
    object Dashboard : Screen("/dashboard")
    object Devices : Screen("/devices")
    object DeviceDetails : Screen("/device_details")
    object DeviceSettings : Screen("/device_settings")

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { append("/$it") }
        }
    }
}