package com.corrot.kwiatonomousapp

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.corrot.kwiatonomousapp.presentation.KwiatonomousNavHost
import com.corrot.kwiatonomousapp.presentation.Screen
import com.corrot.kwiatonomousapp.presentation.theme.KwiatonomousAppTheme
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DashboardScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    lateinit var navController: NavHostController

    @ExperimentalMaterialApi
    @ExperimentalFoundationApi
    @Before
    fun setupNavHost() {
        composeTestRule.setContent {
            navController = rememberNavController()
            KwiatonomousAppTheme(darkTheme = true) {
                Surface(color = MaterialTheme.colors.background) {
                    KwiatonomousNavHost(navController = navController)
                }
            }
        }
    }

    @Test
    fun test_click_on_all_devices_button_opens_devices_screen() {
        composeTestRule.onNodeWithTag("allDevicesButton").apply {
            assertIsDisplayed()
            performClick()
        }

        val route = navController.currentBackStackEntry?.destination?.route
        assertEquals(route, Screen.Devices.route)
    }

    @Test
    fun test_click_on_settings_button_opens_settings_screen() {
        composeTestRule.onNodeWithTag("settingsButton").apply {
            assertIsDisplayed()
            performClick()
        }

        val route = navController.currentBackStackEntry?.destination?.route
        assertEquals(route, Screen.AppSettings.route)
    }
}