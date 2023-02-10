package com.corrot.kwiatonomousapp.presentation.dasboard

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
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
    onLogoutClicked: () -> Unit,
    onLongPressed: (DeviceEvent) -> Unit,
    getDeviceName: (DeviceEvent) -> String?,
) {
    BoxWithConstraints {
        if (maxWidth < 600.dp) {
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                HeaderSection(userName)
                DevicesEventsSection(
                    events = events,
                    isLoading = isLoading,
                    onRefreshEvents = onRefreshEvents,
                    onLongPressed = onLongPressed,
                    getDeviceName = getDeviceName,
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .weight(1f)
                )
                NavigationButtonsSection(
                    onAllDevicesClicked = onAllDevicesClicked,
                    onAppSettingsClicked = onAppSettingsClicked,
                    onLogoutClicked = onLogoutClicked,
                    modifier = Modifier.padding(vertical = 32.dp, horizontal = 12.dp)
                )
            }
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    HeaderSection(userName)
                    NavigationButtonsSection(
                        onAllDevicesClicked = onAllDevicesClicked,
                        onAppSettingsClicked = onAppSettingsClicked,
                        onLogoutClicked = onLogoutClicked,
                        modifier = Modifier
                            .padding(vertical = 16.dp, horizontal = 12.dp)
                            .fillMaxHeight()
                            .width(300.dp)
                    )
                }
                DevicesEventsSection(
                    events = events,
                    isLoading = isLoading,
                    onRefreshEvents = onRefreshEvents,
                    onLongPressed = onLongPressed,
                    getDeviceName = getDeviceName,
                    modifier = Modifier
                        .padding(end = 16.dp, top = 12.dp, bottom = 12.dp)
                        .fillMaxHeight()
                        .weight(1f)
                )
            }
        }
    }
}

@Composable
fun HeaderSection(userName: String?) {
    Row(
        modifier = Modifier.padding(12.dp)
    ) {
        Text(
            text = stringResource(id = R.string.hello_user_format).format(userName),
            style = MaterialTheme.typography.caption,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DevicesEventsSection(
    modifier: Modifier = Modifier,
    events: List<DeviceEvent>?,
    isLoading: Boolean,
    onRefreshEvents: () -> Unit,
    onLongPressed: (DeviceEvent) -> Unit,
    getDeviceName: (DeviceEvent) -> String?,
) {
    val refreshState = rememberPullRefreshState(isLoading, onRefreshEvents)

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clipToBounds() // to avoid PullRefreshIndicator overlapping
            .border(
                width = 1.dp,
                color = Color.Black,
                shape = RoundedCornerShape(CornerSize(8.dp))
            )
    ) {
        events?.let { eventsList ->
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp)
                    .pullRefresh(refreshState)
            ) {
                item { Spacer(modifier = Modifier.height(4.dp)) }
                items(eventsList, key = { e -> e.timestamp }) { event ->
                    DeviceEventItem(
                        deviceName = getDeviceName(event),
                        deviceEvent = event,
                        onLongPressed = { onLongPressed(event) }
                    )
                }
                item { Spacer(modifier = Modifier.height(4.dp)) }
            }
        }
        PullRefreshIndicator(
            refreshing = isLoading,
            state = refreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NavigationButtonsSection(
    modifier: Modifier = Modifier,
    onAllDevicesClicked: () -> Unit,
    onAppSettingsClicked: () -> Unit,
    onLogoutClicked: () -> Unit,
) {
    val cardsModifier = Modifier.fillMaxWidth(.9f)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        modifier = modifier
    ) {
        DashboardCardItem(
            text = stringResource(R.string.all_devices),
            onClicked = onAllDevicesClicked,
            testTag = "allDevicesButton",
            modifier = cardsModifier
        )
        DashboardCardItem(
            text = stringResource(R.string.application_settings),
            onClicked = onAppSettingsClicked,
            testTag = "settingsButton",
            modifier = cardsModifier
        )
        DashboardCardItem(
            text = stringResource(R.string.log_out),
            onClicked = onLogoutClicked,
            testTag = "logOutButton",
            modifier = cardsModifier
        )
    }
}

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Preview(
    name = "DashboardPreviewLight",
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
                    DeviceEvent.Watering(
                        deviceId = "deviceId",
                        timestamp = LocalDateTime.now()
                    ),
                    DeviceEvent.UserNote(
                        userName = "username",
                        title = "title",
                        content = "content",
                        deviceId = "deviceId",
                        timestamp = LocalDateTime.now()
                    ),
                    DeviceEvent.LowBattery(
                        batteryLevel = 55,
                        batteryVoltage = 3.6f,
                        deviceId = "deviceId",
                        timestamp = LocalDateTime.now()
                    ),
                ),
                onRefreshEvents = {},
                onAllDevicesClicked = {},
                onAppSettingsClicked = {},
                onLogoutClicked = {},
                onLongPressed = {},
                getDeviceName = fun(_: DeviceEvent) = "Device name"
            )
        }
    }
}

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Preview(
    name = "DashboardPreviewLightHorizontal",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    widthDp = 700,
    heightDp = 300
)
@Composable
private fun DashboardPreviewPreviewLightHorizontal() {
    KwiatonomousAppTheme(darkTheme = false) {
        Surface {
            DashboardScreenContent(
                padding = PaddingValues(),
                isLoading = false,
                userName = "Username",
                events = listOf(
                    DeviceEvent.Watering(
                        deviceId = "deviceId",
                        timestamp = LocalDateTime.now()
                    ),
                    DeviceEvent.UserNote(
                        userName = "username",
                        title = "title",
                        content = "content",
                        deviceId = "deviceId",
                        timestamp = LocalDateTime.now()
                    ),
                    DeviceEvent.LowBattery(
                        batteryLevel = 55,
                        batteryVoltage = 3.6f,
                        deviceId = "deviceId",
                        timestamp = LocalDateTime.now()
                    ),
                ),
                onRefreshEvents = {},
                onAllDevicesClicked = {},
                onAppSettingsClicked = {},
                onLogoutClicked = {},
                onLongPressed = {},
                getDeviceName = fun(_: DeviceEvent) = "Device name"
            )
        }
    }
}
