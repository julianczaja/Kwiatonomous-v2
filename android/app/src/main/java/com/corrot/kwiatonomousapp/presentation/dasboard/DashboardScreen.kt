package com.corrot.kwiatonomousapp.presentation.dasboard

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.corrot.kwiatonomousapp.R
import com.corrot.kwiatonomousapp.presentation.Screen
import com.corrot.kwiatonomousapp.presentation.dasboard.components.DashboardCardItem

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun DashboardScreen(
    navController: NavController,
//    viewModel: DashboardViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            modifier = Modifier.height(45.dp),
            backgroundColor = MaterialTheme.colors.primary,
            title = { Text(text = stringResource(R.string.app_name)) },
        )
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(12.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .border(
                            width = 1.dp,
                            color = Color.Black,
                            RoundedCornerShape(CornerSize(8.dp))
                        )
                ) {
                    Text(text = "Placeholder")
                }
            }

            LazyVerticalGrid(
                cells = GridCells.Adaptive(150.dp),
                contentPadding = PaddingValues(8.dp),
            ) {
                item {
                    DashboardCardItem(
                        text = stringResource(R.string.all_devices),
                        onClicked = { navController.navigate(Screen.Devices.route) },
                        testTag = "allDevicesButton"
                    )
                }
                item {
                    DashboardCardItem(
                        stringResource(R.string.application_settings),
                        onClicked = { navController.navigate(Screen.AppSettings.route) },
                        testTag = "settingsButton"
                    )
                }
                item {
                    DashboardCardItem(
                        "Placeholder",
                        onClicked = { navController.navigate(Screen.Splash.route) },
                        testTag = ""
                    )
                }
                item {
                    DashboardCardItem(
                        "Placeholder",
                        onClicked = { navController.navigate(Screen.Splash.route) },
                        testTag = ""
                    )
                }
            }
        }
    }
}
