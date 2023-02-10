package com.corrot.kwiatonomousapp.presentation.dasboard

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.corrot.kwiatonomousapp.R
import com.corrot.kwiatonomousapp.common.components.DefaultScaffold
import com.corrot.kwiatonomousapp.common.components.DefaultTopAppBar
import com.corrot.kwiatonomousapp.common.components.DeviceEventItem
import com.corrot.kwiatonomousapp.common.components.WarningBox
import com.corrot.kwiatonomousapp.domain.model.DeviceEvent
import com.corrot.kwiatonomousapp.domain.model.KwiatonomousAppState
import com.corrot.kwiatonomousapp.presentation.Screen
import com.corrot.kwiatonomousapp.presentation.dasboard.components.DashboardCardItem
import com.corrot.kwiatonomousapp.presentation.theme.KwiatonomousAppTheme
import java.time.LocalDateTime

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun DashboardScreen(
    kwiatonomousAppState: KwiatonomousAppState,
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    var isDeleteEventAlertDialogOpened by remember { mutableStateOf(false) }

    LaunchedEffect(true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                DashboardViewModel.Event.LOGGED_OUT -> {
                    kwiatonomousAppState.showSnackbar(context.getString(R.string.logged_out))
                    kwiatonomousAppState.navController.popBackStack()
                    kwiatonomousAppState.navController.navigate(Screen.Login.route)
                }
            }
        }
    }

    DefaultScaffold(
        scaffoldState = kwiatonomousAppState.scaffoldState,
        topBar = { DefaultTopAppBar(title = stringResource(R.string.app_name)) }
    ) { padding ->
        DashboardScreenContent(
            padding = padding,
            isLoading = state.isLoading,
            userName = state.user?.userName,
            events = state.events,
            onRefreshEvents = viewModel::refreshDevicesEvents,
            onAllDevicesClicked = { kwiatonomousAppState.navController.navigate(Screen.Devices.route) },
            onAppSettingsClicked = { kwiatonomousAppState.navController.navigate(Screen.AppSettings.route) },
            onPlaceholderClicked = { kwiatonomousAppState.showSnackbar("Not implemented yet") },
            onLogoutClicked = viewModel::logOut,
            onLongPressed = {
                viewModel.selectEventToDelete(it)
                isDeleteEventAlertDialogOpened = true
            },
            getDeviceName = viewModel::getDeviceNameFromDeviceEvent
        )
        if (isDeleteEventAlertDialogOpened) {
            WarningBox(
                message = stringResource(R.string.user_device_event_delete_warning_message),
                onCancelClicked = {
                    isDeleteEventAlertDialogOpened = false
                },
                onConfirmClicked = {
                    isDeleteEventAlertDialogOpened = false
                    viewModel.deleteSelectedUserEvent()
                }
            )
        }
    }
}

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun DashboardScreenContent(
    padding: PaddingValues,
    isLoading: Boolean,
    userName: String?,
    events: List<DeviceEvent>?,
    onRefreshEvents: () -> Unit,
    onAllDevicesClicked: () -> Unit,
    onAppSettingsClicked: () -> Unit,
    onPlaceholderClicked: () -> Unit,
    onLogoutClicked: () -> Unit,
    onLongPressed: (DeviceEvent) -> Unit,
    getDeviceName: (DeviceEvent) -> String?,
) {
    val refreshState = rememberPullRefreshState(isLoading, onRefreshEvents)

    Box(modifier = Modifier.pullRefresh(refreshState)) {
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
                    text = stringResource(id = R.string.hello_user_format).format(userName),
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
                    events?.let { eventsList ->
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp)
                        ) {
                            items(eventsList, key = { e -> e.timestamp }) { event ->
                                DeviceEventItem(
                                    deviceName = getDeviceName(event),
                                    deviceEvent = event,
                                    onLongPressed = { onLongPressed(event) }
                                )
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
                        onClicked = onAllDevicesClicked,
                        testTag = "allDevicesButton"
                    )
                }
                item {
                    DashboardCardItem(
                        stringResource(R.string.application_settings),
                        onClicked = onAppSettingsClicked,
                        testTag = "settingsButton"
                    )
                }
                item {
                    DashboardCardItem(
                        text = "",
                        onClicked = onPlaceholderClicked,
                        testTag = ""
                    )
                }
                item {
                    DashboardCardItem(
                        stringResource(R.string.log_out),
                        onClicked = onLogoutClicked,
                        testTag = "logOutButton"
                    )
                }
            }
        }
        PullRefreshIndicator(
            refreshing = isLoading,
            state = refreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
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
            DashboardScreenContent(
                padding = PaddingValues(),
                isLoading = false,
                userName = "Username",
                events = listOf(
                    DeviceEvent.Watering("deviceId", LocalDateTime.now()),
                    DeviceEvent.UserNote("username", "title", "content", "deviceId", LocalDateTime.now()),
                    DeviceEvent.LowBattery(55, 3.6f, "deviceId", LocalDateTime.now()),
                ),
                onRefreshEvents = {},
                onAllDevicesClicked = {},
                onAppSettingsClicked = {},
                onPlaceholderClicked = {},
                onLogoutClicked = {},
                onLongPressed = {},
                getDeviceName = fun(_: DeviceEvent) = "Device name"
            )
        }
    }
}
