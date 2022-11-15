package com.corrot.kwiatonomousapp.presentation.device_details

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.corrot.kwiatonomousapp.R
import com.corrot.kwiatonomousapp.common.Constants
import com.corrot.kwiatonomousapp.common.components.*
import com.corrot.kwiatonomousapp.common.components.chart.DateLineChart
import com.corrot.kwiatonomousapp.common.components.chart.LineChartDataType
import com.corrot.kwiatonomousapp.common.components.chart.LineChartDateType
import com.corrot.kwiatonomousapp.common.components.chart.mapToString
import com.corrot.kwiatonomousapp.common.toLong
import com.corrot.kwiatonomousapp.domain.model.*
import com.corrot.kwiatonomousapp.presentation.Screen
import com.corrot.kwiatonomousapp.common.components.DefaultScaffold
import com.corrot.kwiatonomousapp.common.isScrolled
import com.corrot.kwiatonomousapp.common.isScrollingUp
import com.corrot.kwiatonomousapp.presentation.device_details.components.*
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import timber.log.Timber
import java.time.LocalDateTime

@Composable
fun DeviceDetailsScreen(
    kwiatonomousAppState: KwiatonomousAppState,
    viewModel: DeviceDetailsViewModel = hiltViewModel(),
) {
    val state = viewModel.state.value
    val currentAppTheme = viewModel.currentAppTheme.collectAsState(initial = AppTheme.AUTO)
    val currentChartSettings = viewModel.currentChartSettings.collectAsState(initial = ChartSettings())
    val isDarkMode = when (currentAppTheme.value) {
        AppTheme.AUTO -> isSystemInDarkTheme()
        AppTheme.LIGHT -> false
        AppTheme.DARK -> true
    }
    var isDeleteEventAlertDialogOpened by remember { mutableStateOf(false) }
    var isEventsContentScrolled by remember { mutableStateOf(false) }
    var isContentScrollingUp by remember { mutableStateOf(false) }
    var isDeleteDeviceAlertDialogOpened by remember { mutableStateOf(false) }
    var addNoteDialogOpened by remember { mutableStateOf(false) }
    var addWateringDialogOpened by remember { mutableStateOf(false) }
    var addPumpCleaningDialogOpened by remember { mutableStateOf(false) }

    LaunchedEffect(true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                DeviceDetailsViewModel.Event.NAVIGATE_UP -> kwiatonomousAppState.navController.navigateUp()
                DeviceDetailsViewModel.Event.OPEN_EDIT_USER_DEVICE_SCREEN -> kwiatonomousAppState.navController.navigate(
                    Screen.AddEditUserDevice.withOptionalArg(
                        argName = Constants.NAV_ARG_DEVICE_ID,
                        argValue = viewModel.state.value.userDevice!!.deviceId // it can't be null there
                    )
                )
                DeviceDetailsViewModel.Event.SHOW_DELETE_ALERT_DIALOG -> {
                    isDeleteDeviceAlertDialogOpened = true
                }
            }
        }
    }

    DefaultScaffold(
        scaffoldState = kwiatonomousAppState.scaffoldState,
        topBar = {
            DefaultTopAppBar(
                title = stringResource(R.string.device_details),
                onNavigateBackClicked = { kwiatonomousAppState.navController.popBackStack() },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.toggleDeviceNotifications()
                        }
                    ) {
                        if (state.userDevice?.notificationsOn == true) {
                            Image(
                                imageVector = ImageVector.vectorResource(id = R.drawable.ic_notifications_on),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Image(
                                imageVector = ImageVector.vectorResource(id = R.drawable.ic_notifications_off),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            ExpandableFloatingActionButton(
                isVisible = isContentScrollingUp || !isEventsContentScrolled,
                items = listOf(
                    ExpandableFloatingActionButtonItem(
                        strokeColor = MaterialTheme.colors.secondary,
                        fillColor = MaterialTheme.colors.surface,
                        imageId = R.drawable.watering,
                        onItemClick = { addWateringDialogOpened = !addWateringDialogOpened }
                    ),
                    ExpandableFloatingActionButtonItem(
                        strokeColor = MaterialTheme.colors.secondary,
                        fillColor = MaterialTheme.colors.surface,
                        imageId = R.drawable.note,
                        onItemClick = { addNoteDialogOpened = !addNoteDialogOpened }
                    ),
                    ExpandableFloatingActionButtonItem(
                        strokeColor = MaterialTheme.colors.secondary,
                        fillColor = MaterialTheme.colors.surface,
                        imageId = R.drawable.pump_cleaning, // TODO: replace with pump
                        onItemClick = { addPumpCleaningDialogOpened = !addPumpCleaningDialogOpened }
                    )
                )
            )
        }
    ) { padding ->
        SwipeRefresh(
            state = rememberSwipeRefreshState(viewModel.isLoading),
            onRefresh = { viewModel.refreshData() },
            Modifier.padding(padding)
        ) {
            val scroll = rememberLazyListState()
            isContentScrollingUp = scroll.isScrollingUp()

            LazyColumn(
                state = scroll,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp)
            ) {

                item { Spacer(Modifier.height(16.dp)) }

                state.userDevice?.let { userDevice ->
                    item {
                        UserDeviceSection(
                            userDevice = userDevice,
                            lastUpdate = state.deviceUpdates?.firstOrNull(),
                            onActionClicked = {
                                viewModel.onUserDeviceAction(it)
                            }
                        )
                    }
                }

                state.device?.let { device ->
                    item {
                        DeviceSection(device)
                    }
                }

                state.deviceConfiguration?.let { deviceConfiguration ->
                    item {
                        DeviceConfigurationSection(
                            deviceConfiguration,
                            onEditClicked = {
                                kwiatonomousAppState.navController.navigate(
                                    Screen.DeviceSettings.withArgs(
                                        deviceConfiguration.deviceId
                                    )
                                )
                            }
                        )
                    }
                }

                if (state.deviceUpdates != null) {
                    item {
                        DeviceUpdatesSection(
                            deviceUpdates = state.deviceUpdates,
                            selectedChartDateType = viewModel.state.value.selectedChartDateType,
                            selectedDateRange = viewModel.state.value.selectedDateRange,
                            selectedChartDataType = viewModel.state.value.selectedChartDataType,
                            chartSettings = currentChartSettings.value,
                            isDarkMode = isDarkMode,
                            onChartDateTypeSelected = { viewModel.onChartDateTypeSelected(it) },
                            onChartDataTypeSelected = { viewModel.onChartDataTypeSelected(it) },
                            onChartSettingsClicked = {
                                kwiatonomousAppState.navController.navigate(Screen.AppSettings.route)
                            }
                        )
                    }
                }

                if (state.deviceEvents != null) {
                    item {
                        DeviceEventsSection(
                            deviceEvents = state.deviceEvents,
                            onScrolled = { isScrolled ->
                                Timber.e("EVENTS SCROLLED $isScrolled")
                                isEventsContentScrolled = isScrolled
                            },
                            onLongPressed = {
                                viewModel.selectEventToDelete(it)
                                isDeleteEventAlertDialogOpened = true
                            }
                        )
                    }
                }

                item { Spacer(Modifier.height(16.dp)) }
            }
        }
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
        if (isDeleteDeviceAlertDialogOpened) {
            WarningBox(
                message = stringResource(R.string.user_device_delete_warning_message),
                onCancelClicked = {
                    isDeleteDeviceAlertDialogOpened = false
                },
                onConfirmClicked = {
                    isDeleteDeviceAlertDialogOpened = false
                    viewModel.deleteUserDevice()
                }
            )
        }
        if (addNoteDialogOpened) {
            AddNoteDialog(
                title = state.noteTitle,
                content = state.noteContent,
                onTitleChange = { viewModel.onNoteTitleChanged(it) },
                onContentChange = { viewModel.onNoteContentChanged(it) },
                onAddClicked = {
                    addNoteDialogOpened = false
                    viewModel.onAddNoteClicked()
                },
                onCancelClicked = { addNoteDialogOpened = false }
            )
        }
        if (addWateringDialogOpened) {
            AddWateringEventDialog(
                dateTime = LocalDateTime.now(),
                onDateTimeChange = {},
                onAddClicked = {
                    addWateringDialogOpened = false
                    viewModel.onAddWateringEventClicked()
                },
                onCancelClicked = { addWateringDialogOpened = false }
            )
        }
        if (addPumpCleaningDialogOpened) {
            AddWateringEventDialog( // TODO: AddPumpCleaningEventDialog or generic
                dateTime = LocalDateTime.now(),
                onDateTimeChange = {},
                onAddClicked = {
                    addPumpCleaningDialogOpened = false
                    viewModel.onAddPumpCleaningEventClicked()
                },
                onCancelClicked = { addPumpCleaningDialogOpened = false }
            )
        }
        state.error?.let { error ->
            ErrorBoxCancelRetry(
                message = error,
                onCancel = { viewModel.confirmError() },
                onRetry = {
                    viewModel.confirmError()
                    viewModel.refreshData()
                }
            )
        }
    }
}

@Composable
private fun UserDeviceSection(
    userDevice: UserDevice,
    lastUpdate: DeviceUpdate?,
    onActionClicked: (UserDeviceAction) -> Unit,
) {
    val isExpanded = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        UserDeviceItem(userDevice, lastUpdate)
        Box(
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            IconButton(
                onClick = {
                    isExpanded.value = true
                }
            ) {
                Icon(Icons.Filled.MoreVert, "")
            }
            DropdownMenu(
                expanded = isExpanded.value,
                onDismissRequest = { isExpanded.value = false },
            ) {
                DropdownMenuItem(
                    onClick = {
                        isExpanded.value = false
                        onActionClicked(UserDeviceAction.EDIT)
                    },
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.width(100.dp)
                    ) {
                        Icon(Icons.Filled.Edit, "")
                        Text(
                            stringResource(R.string.edit),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
                DropdownMenuItem(
                    onClick = {
                        isExpanded.value = false
                        onActionClicked(UserDeviceAction.DELETE)
                    }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.width(100.dp)
                    ) {
                        Icon(Icons.Filled.Delete, "")
                        Text(
                            stringResource(R.string.delete),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }
    }
    Divider(
        color = MaterialTheme.colors.primaryVariant,
        thickness = 1.dp,
        modifier = Modifier.padding(vertical = 16.dp)
    )
}

@Composable
private fun DeviceSection(device: Device) {
    DeviceItem(device)
    Divider(
        color = MaterialTheme.colors.primaryVariant,
        thickness = 1.dp,
        modifier = Modifier.padding(vertical = 16.dp)
    )
}

@Composable
private fun DeviceConfigurationSection(
    deviceConfiguration: DeviceConfiguration,
    onEditClicked: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        ExpandableCardWithLabel(
            title = stringResource(R.string.device_configuration),
            initialExpandedState = true
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                DeviceConfigurationItem(
                    deviceConfiguration = deviceConfiguration
                )
                Box(
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    IconButton(onClick = { onEditClicked() }) {
                        Icon(Icons.Filled.Edit, "")
                    }
                }
            }
        }
        Divider(
            color = MaterialTheme.colors.primaryVariant,
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 16.dp)
        )
    }
}

@Composable
private fun DeviceUpdatesSection(
    deviceUpdates: List<DeviceUpdate>,
    selectedChartDateType: LineChartDateType,
    selectedChartDataType: LineChartDataType,
    selectedDateRange: Pair<Long, Long>,
    isDarkMode: Boolean,
    chartSettings: ChartSettings,
    onChartDateTypeSelected: (LineChartDateType) -> Unit,
    onChartDataTypeSelected: (LineChartDataType) -> Unit,
    onChartSettingsClicked: () -> Unit,
) {
    var updatesTableDialogOpened by remember { mutableStateOf(false) }

    ExpandableCardWithLabel(
        title = stringResource(R.string.last_updates),
        initialExpandedState = true
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Card(
                shape = RoundedCornerShape(8.dp),
                elevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(
                            enabled = deviceUpdates.isNotEmpty(),
                            onClick = {
                                updatesTableDialogOpened = true
                            },
                            modifier = Modifier
                                .padding(top = 4.dp, end = 8.dp)
                                .then(Modifier.size(24.dp))
                        ) {
                            Icon(Icons.Filled.List, "")
                        }
                        IconButton(
                            onClick = onChartSettingsClicked,
                            modifier = Modifier
                                .padding(top = 4.dp, end = 4.dp)
                                .then(Modifier.size(24.dp))
                        ) {
                            Icon(Icons.Filled.Settings, "")
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    ) {
                        DateLineChart(
                            xData = deviceUpdates.map { it.updateTime.toLong() },
                            yData = when (selectedChartDataType) {
                                LineChartDataType.TEMPERATURE -> deviceUpdates.map { it.temperature }
                                LineChartDataType.HUMIDITY -> deviceUpdates.map { it.humidity }
                                LineChartDataType.BATTERY -> deviceUpdates.map { it.batteryVoltage }
                            },
                            fromDate = selectedDateRange.first,
                            toDate = selectedDateRange.second,
                            dateType = selectedChartDateType,
                            dataType = selectedChartDataType,
                            renderDropLines = chartSettings.renderDropLines,
                            renderGridLines = chartSettings.renderGridLines,
                            isDarkTheme = isDarkMode
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        CustomRadioGroup(
                            options = LineChartDateType.values()
                                .map { it.mapToString(LocalContext.current) },
                            selectedIndex = selectedChartDateType.ordinal,
                            onOptionSelected = { onChartDateTypeSelected(LineChartDateType.values()[it]) }
                        )
                        CustomRadioGroup(
                            options = LineChartDataType.values()
                                .map { it.mapToString(LocalContext.current) },
                            selectedIndex = selectedChartDataType.ordinal,
                            onOptionSelected = { onChartDataTypeSelected(LineChartDataType.values()[it]) }
                        )
                    }
                }
            }
        }
    }
    if (updatesTableDialogOpened) {
        DeviceUpdatesTable(
            deviceUpdates = deviceUpdates,
            onDismiss = { updatesTableDialogOpened = false })
    }
    Divider(
        color = MaterialTheme.colors.primaryVariant,
        thickness = 1.dp,
        modifier = Modifier.padding(vertical = 16.dp)
    )
}


@Composable
fun DeviceEventsSection(
    deviceEvents: List<DeviceEvent>,
    onScrolled: (Boolean) -> Unit,
    onLongPressed: (DeviceEvent) -> Unit,
) {
    val scroll = rememberLazyListState()
    onScrolled(scroll.isScrolled)

    ExpandableBoxWithLabel(
        title = stringResource(R.string.events_label),
        initialExpandedState = true
    ) {
        LazyColumn(
            state = scroll,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(8.dp)
        ) {
            deviceEvents.forEachIndexed { index, event ->
                item {
                    DeviceEventItem(
                        deviceName = null,
                        deviceEvent = event,
                        onLongPressed = { onLongPressed(event) }
                    )
                }
                if (index < deviceEvents.size - 1) {
                    item { Spacer(modifier = Modifier.height(8.dp)) }
                }
            }
        }
    }
}
