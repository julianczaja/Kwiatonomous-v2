package com.corrot.kwiatonomousapp.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.corrot.kwiatonomousapp.common.Constants.NAV_ARG_DEVICE_ID
import com.corrot.kwiatonomousapp.domain.NotificationsManager
import com.corrot.kwiatonomousapp.domain.model.KwiatonomousAppState
import com.corrot.kwiatonomousapp.presentation.add_user_device.AddEditUserDeviceScreen
import com.corrot.kwiatonomousapp.presentation.app_settings.AppSettingsScreen
import com.corrot.kwiatonomousapp.presentation.dasboard.DashboardScreen
import com.corrot.kwiatonomousapp.presentation.device_details.DeviceDetailsScreen
import com.corrot.kwiatonomousapp.presentation.device_settings.DeviceSettingsScreen
import com.corrot.kwiatonomousapp.presentation.devices.DevicesScreen
import com.corrot.kwiatonomousapp.presentation.login.LoginScreen
import com.corrot.kwiatonomousapp.presentation.register.RegisterScreen
import com.corrot.kwiatonomousapp.presentation.splashscreen.SplashScreen


@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun KwiatonomousNavHost(
    kwiatonomousAppState: KwiatonomousAppState,
    startDestination: String = Screen.Dashboard.route
) {
    NavHost(
        navController = kwiatonomousAppState.navController,
        startDestination = startDestination
    ) {
        composable(route = Screen.Splash.route) {
            SplashScreen(kwiatonomousAppState)
        }

        composable(route = Screen.Register.route) {
            RegisterScreen(kwiatonomousAppState)
        }

        composable(route = Screen.Login.route) {
            LoginScreen(kwiatonomousAppState)
        }

        composable(route = Screen.Devices.route) {
            DevicesScreen(kwiatonomousAppState)
        }

        composable(
            route = Screen.DeviceDetails.route + "/{$NAV_ARG_DEVICE_ID}",
            arguments = listOf(navArgument(name = NAV_ARG_DEVICE_ID) {
                type = NavType.StringType
                nullable = false
            })
        ) {
            DeviceDetailsScreen(kwiatonomousAppState)
        }

        composable(
            route = Screen.AppSettings.route
        ) {
            AppSettingsScreen(kwiatonomousAppState, NotificationsManager())
        }

        composable(
            route = Screen.Dashboard.route
        ) {
            DashboardScreen(kwiatonomousAppState)
        }

        composable(
            route = Screen.DeviceSettings.route + "/{$NAV_ARG_DEVICE_ID}",
            arguments = listOf(navArgument(name = NAV_ARG_DEVICE_ID) {
                type = NavType.StringType
                nullable = false
            })
        ) {
            DeviceSettingsScreen(kwiatonomousAppState)
        }

        composable(
            route = Screen.AddEditUserDevice.route + "?$NAV_ARG_DEVICE_ID={$NAV_ARG_DEVICE_ID}",
            arguments = listOf(navArgument(name = NAV_ARG_DEVICE_ID) {
                type = NavType.StringType
                nullable = true
            })
        ) {
            AddEditUserDeviceScreen(kwiatonomousAppState)
        }
    }
}

sealed class Screen(val route: String) {
    data object Splash : Screen("/splash")
    data object Register : Screen("/register")
    data object Login : Screen("/login")
    data object Dashboard : Screen("/dashboard")
    data object Devices : Screen("/devices")
    data object AppSettings : Screen("/app_settings")
    data object DeviceDetails : Screen("/device_details")
    data object DeviceSettings : Screen("/device_settings")
    data object AddEditUserDevice : Screen("/add_edit_user_device")

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { append("/$it") }
        }
    }

    fun withOptionalArg(argName: String, argValue: String) = "$route?$argName=$argValue"

}