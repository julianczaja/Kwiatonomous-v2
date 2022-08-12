package com.corrot.kwiatonomousapp.presentation.dasboard

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.corrot.kwiatonomousapp.KwiatonomousAppState
import com.corrot.kwiatonomousapp.R
import com.corrot.kwiatonomousapp.common.components.DefaultTopAppBar
import com.corrot.kwiatonomousapp.common.components.DeviceEventItem
import com.corrot.kwiatonomousapp.presentation.Screen
import com.corrot.kwiatonomousapp.presentation.dasboard.components.DashboardCardItem
import com.corrot.kwiatonomousapp.presentation.theme.KwiatonomousAppTheme

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun DashboardScreen(
    kwiatonomousAppState: KwiatonomousAppState,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state = viewModel.state

    LaunchedEffect(true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                DashboardViewModel.Event.LOGGED_OUT -> {
                    kwiatonomousAppState.showSnackbar("Logged out") // FIXME
                    kwiatonomousAppState.navController.popBackStack()
                    kwiatonomousAppState.navController.navigate(Screen.Login.route)
                }
            }
        }
    }

    Scaffold(
        scaffoldState = kwiatonomousAppState.scaffoldState,
        topBar = {
            DefaultTopAppBar(title = stringResource(R.string.app_name))
        }
    ) { padding ->
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.hello_user_format).format(state.value.user?.userId),
                    style = MaterialTheme.typography.caption,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
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
                    state.value.events?.let {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp)
                        ) {
                            it.forEachIndexed { index, event ->
                                item { DeviceEventItem(deviceEvent = event) }
                                if (index < it.size - 1) {
                                    item { Spacer(modifier = Modifier.height(8.dp)) }
                                }
                            }
                        }
                    }
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Adaptive(150.dp),
                contentPadding = PaddingValues(8.dp),
            ) {
                item {
                    DashboardCardItem(
                        text = stringResource(R.string.all_devices),
                        onClicked = { kwiatonomousAppState.navController.navigate(Screen.Devices.route) },
                        testTag = "allDevicesButton"
                    )
                }
                item {
                    DashboardCardItem(
                        stringResource(R.string.application_settings),
                        onClicked = { kwiatonomousAppState.navController.navigate(Screen.AppSettings.route) },
                        testTag = "settingsButton"
                    )
                }
                item {
                    DashboardCardItem(
                        text = "",
                        onClicked = { kwiatonomousAppState.showSnackbar("Not implemented yet") },
                        testTag = ""
                    )
                }
                item {
                    DashboardCardItem(
                        stringResource(R.string.log_out),
                        onClicked = {
                            viewModel.logOut()
                        },
                        testTag = "logOutButton"
                    )
                }
            }
        }
    }
}

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Preview(
    "DashboardPreviewLight",
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
private fun DashboardPreviewPreviewLight() {
    KwiatonomousAppTheme(darkTheme = false) {
        Surface {
            DashboardScreen(
                KwiatonomousAppState(
                    navController = rememberNavController(),
                    scaffoldState = rememberScaffoldState(),
                    snackbarScope = rememberCoroutineScope()
                )
            )
        }
    }
}
