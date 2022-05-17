package com.corrot.kwiatonomousapp.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.corrot.kwiatonomousapp.common.Constants.NAV_ARG_DEVICE_ID
import com.corrot.kwiatonomousapp.presentation.add_user_device.AddEditUserDeviceScreen
import com.corrot.kwiatonomousapp.presentation.app_settings.AppSettingsScreen
import com.corrot.kwiatonomousapp.presentation.dasboard.DashboardScreen
import com.corrot.kwiatonomousapp.presentation.device_details.DeviceDetailsScreen
import com.corrot.kwiatonomousapp.presentation.device_settings.DeviceSettingsScreen
import com.corrot.kwiatonomousapp.presentation.devices.DevicesScreen
import com.corrot.kwiatonomousapp.presentation.login.LoginScreen
import com.corrot.kwiatonomousapp.presentation.splashscreen.SplashScreen
import com.google.accompanist.pager.ExperimentalPagerApi

@ExperimentalPagerApi
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun KwiatonomousNavHost(
    navController: NavHostController,
    startDestination: String = Screen.Dashboard.route
) {
    NavHost(navController = navController, startDestination = startDestination) {

        composable(route = Screen.Splash.route) {
            SplashScreen(navController)
        }

        composable(route = Screen.Login.route) {
            LoginScreen(navController)
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
            route = Screen.AppSettings.route
        ) {
            AppSettingsScreen(navController)
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

        composable(
            route = Screen.AddEditUserDevice.route + "?$NAV_ARG_DEVICE_ID={$NAV_ARG_DEVICE_ID}",
            arguments = listOf(navArgument(name = NAV_ARG_DEVICE_ID) {
                type = NavType.StringType
                nullable = true
            })
        ) {
            AddEditUserDeviceScreen(navController)
        }
    }
}

sealed class Screen(val route: String) {
    object Splash : Screen("/splash")
    object Login : Screen("/login")
    object Dashboard : Screen("/dashboard")
    object Devices : Screen("/devices")
    object AppSettings : Screen("/app_settings")
    object DeviceDetails : Screen("/device_details")
    object DeviceSettings : Screen("/device_settings")
    object AddEditUserDevice : Screen("/add_edit_user_device")

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { append("/$it") }
        }
    }

    fun withOptionalArg(argName: String, argValue: String) = "$route?$argName=$argValue"

}