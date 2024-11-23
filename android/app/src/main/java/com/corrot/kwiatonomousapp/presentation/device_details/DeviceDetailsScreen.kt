package com.corrot.kwiatonomousapp.presentation.device_details

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.corrot.kwiatonomousapp.R
import com.corrot.kwiatonomousapp.common.Constants
import com.corrot.kwiatonomousapp.common.components.CustomRadioGroup
import com.corrot.kwiatonomousapp.common.components.DefaultScaffold
import com.corrot.kwiatonomousapp.common.components.DefaultTopAppBar
import com.corrot.kwiatonomousapp.common.components.DeviceEventItem
import com.corrot.kwiatonomousapp.common.components.ErrorBoxCancelRetry
import com.corrot.kwiatonomousapp.common.components.ExpandableBoxWithLabel
import com.corrot.kwiatonomousapp.common.components.ExpandableCardWithLabel
import com.corrot.kwiatonomousapp.common.components.ExpandableFloatingActionButton
import com.corrot.kwiatonomousapp.common.components.ExpandableFloatingActionButtonItem
import com.corrot.kwiatonomousapp.common.components.UserDeviceItem
import com.corrot.kwiatonomousapp.common.components.WarningBox
import com.corrot.kwiatonomousapp.common.components.chart.DateLineChart
import com.corrot.kwiatonomousapp.common.components.chart.LineChartDataType
import com.corrot.kwiatonomousapp.common.components.chart.LineChartDateType
import com.corrot.kwiatonomousapp.common.components.chart.mapToString
import com.corrot.kwiatonomousapp.common.isScrolled
import com.corrot.kwiatonomousapp.common.isScrollingUp
import com.corrot.kwiatonomousapp.common.toLong
import com.corrot.kwiatonomousapp.domain.model.AppTheme
import com.corrot.kwiatonomousapp.domain.model.ChartSettings
import com.corrot.kwiatonomousapp.domain.model.Device
import com.corrot.kwiatonomousapp.domain.model.DeviceConfiguration
import com.corrot.kwiatonomousapp.domain.model.DeviceEvent
import com.corrot.kwiatonomousapp.domain.model.DeviceUpdate
import com.corrot.kwiatonomousapp.domain.model.KwiatonomousAppState
import com.corrot.kwiatonomousapp.domain.model.UserDevice
import com.corrot.kwiatonomousapp.domain.model.UserDeviceAction
import com.corrot.kwiatonomousapp.domain.model.isDarkMode
import com.corrot.kwiatonomousapp.presentation.Screen
import com.corrot.kwiatonomousapp.presentation.device_details.components.AddNoteDialog
import com.corrot.kwiatonomousapp.presentation.device_details.components.AddWateringEventDialog
import com.corrot.kwiatonomousapp.presentation.device_details.components.DeviceConfigurationItem
import com.corrot.kwiatonomousapp.presentation.device_details.components.DeviceItem
import com.corrot.kwiatonomousapp.presentation.device_details.components.DeviceUpdatesTable
import java.time.LocalDateTime

@Composable
fun DeviceDetailsScreen(
    kwiatonomousAppState: KwiatonomousAppState,
    viewModel: DeviceDetailsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val currentAppTheme = viewModel.currentAppTheme.collectAsState(initial = AppTheme.AUTO)
    val currentChartSettings = viewModel.currentChartSettings.collectAsState(initial = ChartSettings())
    val isDarkMode = currentAppTheme.value.isDarkMode(isSystemInDarkTheme())

    var addNoteDialogOpened by remember { mutableStateOf(false) }
    var addWateringDialogOpened by remember { mutableStateOf(false) }
    var addPumpCleaningDialogOpened by remember { mutableStateOf(false) }
    var isDeleteUserDeviceAlertDialogOpened by remember { mutableStateOf(false) }
    var isDeleteEventAlertDialogOpened by remember { mutableStateOf(false) }
    var isEventsContentScrolled by remember { mutableStateOf(false) }
    var isContentScrollingUp by remember { mutableStateOf(false) }

    val scrollState = rememberLazyListState()
    isContentScrollingUp = scrollState.isScrollingUp()

    LaunchedEffect(true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                DeviceDetailsViewModel.Event.NAVIGATE_UP -> kwiatonomousAppState.navController.navigateUp()
                DeviceDetailsViewModel.Event.OPEN_EDIT_USER_DEVICE_SCREEN -> kwiatonomousAppState.navController.navigate(
                    Screen.AddEditUserDevice.withOptionalArg(
                        argName = Constants.NAV_ARG_DEVICE_ID,
                        argValue = state.userDevice!!.deviceId
                    )
                )
                DeviceDetailsViewModel.Event.SHOW_DELETE_ALERT_DIALOG -> {
                    isDeleteUserDeviceAlertDialogOpened = true
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
                        imageId = R.drawable.pump_cleaning,
                        onItemClick = { addPumpCleaningDialogOpened = !addPumpCleaningDialogOpened }
                    )
                )
            )
        }
    ) { padding ->
        DeviceDetailsScreenContent(
            error = state.error,
            isLoading = viewModel.isLoading,
            isDarkMode = isDarkMode,
            userDevice = state.userDevice,
            device = state.device,
            deviceConfiguration = state.deviceConfiguration,
            deviceUpdates = state.deviceUpdates,
            deviceEvents = state.deviceEvents,
            scrollState = scrollState,
            noteTitle = state.noteTitle,
            noteContent = state.noteContent,
            isDeleteUserDeviceAlertDialogOpened = isDeleteUserDeviceAlertDialogOpened,
            isDeleteEventAlertDialogOpened = isDeleteEventAlertDialogOpened,
            addNoteDialogOpened = addNoteDialogOpened,
            addWateringDialogOpened = addWateringDialogOpened,
            addPumpCleaningDialogOpened = addPumpCleaningDialogOpened,
            selectedChartDateType = state.selectedChartDateType,
            selectedChartDataType = state.selectedChartDataType,
            selectedDateRange = state.selectedDateRange,
            currentChartSettings = currentChartSettings.value,
            onChartDateTypeSelected = viewModel::onChartDateTypeSelected,
            onChartDataTypeSelected = viewModel::onChartDataTypeSelected,
            onConfirmError = viewModel::confirmError,
            onRefreshData = viewModel::refreshData,
            onDeviceEventsSectionScrolled = { isEventsContentScrolled = it },
            onChartSettingsClicked = { kwiatonomousAppState.navController.navigate(Screen.AppSettings.route) },
            onUserDeviceAction = viewModel::onUserDeviceAction,
            onDeviceEventLongPress = {
                viewModel.selectEventToDelete(it)
                isDeleteEventAlertDialogOpened = true
            },
            onEditDeviceConfigurationClicked = {
                kwiatonomousAppState.navController.navigate(Screen.DeviceSettings.withArgs(state.userDevice!!.deviceId))
            },
            onDeleteUserDevice = {
                viewModel.deleteUserDevice()
                isDeleteUserDeviceAlertDialogOpened = false
            },
            onNoteTitleChanged = viewModel::onNoteTitleChanged,
            onNoteContentChanged = viewModel::onNoteContentChanged,
            onDeleteUserDeviceCancelled = { isDeleteUserDeviceAlertDialogOpened = false },
            onDeleteSelectedUserEvent = {
                viewModel.deleteSelectedUserEvent()
                isDeleteEventAlertDialogOpened = false
            },
            onDeleteSelectedUserEventCancelled = { isDeleteEventAlertDialogOpened = false },
            onAddPumpCleaningEvent = {
                addPumpCleaningDialogOpened = false
                viewModel.addPumpCleaningEvent()
            },
            onAddPumpCleaningEventCancelled = { addPumpCleaningDialogOpened = false },
            onAddWateringEvent = {
                addWateringDialogOpened = false
                viewModel.addWateringEvent()
            },
            onAddWateringEventCancelled = { addWateringDialogOpened = false },
            onAddNote = {
                addNoteDialogOpened = false
                viewModel.addNote()
            },
            onAddNoteCancelled = { addNoteDialogOpened = false }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DeviceDetailsScreenContent(
    error: String?,
    isLoading: Boolean,
    isDarkMode: Boolean,
    userDevice: UserDevice?,
    device: Device?,
    deviceConfiguration: DeviceConfiguration?,
    deviceUpdates: List<DeviceUpdate>?,
    deviceEvents: List<DeviceEvent>?,
    scrollState: LazyListState,
    noteTitle: String,
    noteContent: String,
    isDeleteUserDeviceAlertDialogOpened: Boolean,
    isDeleteEventAlertDialogOpened: Boolean,
    addNoteDialogOpened: Boolean,
    addWateringDialogOpened: Boolean,
    addPumpCleaningDialogOpened: Boolean,
    selectedChartDateType: LineChartDateType,
    selectedChartDataType: LineChartDataType,
    selectedDateRange: Pair<Long, Long>,
    currentChartSettings: ChartSettings,
    onChartDateTypeSelected: (LineChartDateType) -> Unit,
    onChartDataTypeSelected: (LineChartDataType) -> Unit,
    onConfirmError: () -> Unit,
    onRefreshData: () -> Unit,
    onDeviceEventsSectionScrolled: (Boolean) -> Unit,
    onChartSettingsClicked: () -> Unit,
    onUserDeviceAction: (UserDeviceAction) -> Unit,
    onDeviceEventLongPress: (DeviceEvent) -> Unit,
    onEditDeviceConfigurationClicked: () -> Unit,
    onNoteTitleChanged: (String) -> Unit,
    onNoteContentChanged: (String) -> Unit,
    onDeleteUserDevice: () -> Unit,
    onDeleteUserDeviceCancelled: () -> Unit,
    onDeleteSelectedUserEvent: () -> Unit,
    onDeleteSelectedUserEventCancelled: () -> Unit,
    onAddPumpCleaningEvent: () -> Unit,
    onAddPumpCleaningEventCancelled: () -> Unit,
    onAddWateringEvent: () -> Unit,
    onAddWateringEventCancelled: () -> Unit,
    onAddNote: () -> Unit,
    onAddNoteCancelled: () -> Unit,
) {
    val refreshState = rememberPullRefreshState(isLoading, onRefreshData)

    Box(Modifier.pullRefresh(refreshState)) {
        LazyColumn(
            state = scrollState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp)
        ) {
            item { Spacer(Modifier.height(16.dp)) }
            if (userDevice != null) {
                item {
                    UserDeviceSection(
                        userDevice = userDevice,
                        lastUpdate = deviceUpdates?.firstOrNull(),
                        onActionClicked = onUserDeviceAction
                    )
                }
            }
            if (device != null) {
                item {
                    DeviceSection(device)
                }
            }
            if (deviceConfiguration != null) {
                item {
                    DeviceConfigurationSection(
                        deviceConfiguration = deviceConfiguration,
                        onEditClicked = onEditDeviceConfigurationClicked
                    )
                }
            }
            if (deviceUpdates != null) {
                item {
                    DeviceUpdatesSection(
                        deviceUpdates = deviceUpdates,
                        selectedChartDateType = selectedChartDateType,
                        selectedDateRange = selectedDateRange,
                        selectedChartDataType = selectedChartDataType,
                        chartSettings = currentChartSettings,
                        isDarkMode = isDarkMode,
                        onChartDateTypeSelected = onChartDateTypeSelected,
                        onChartDataTypeSelected = onChartDataTypeSelected,
                        onChartSettingsClicked = onChartSettingsClicked
                    )
                }
            }
            if (deviceEvents != null) {
                item {
                    DeviceEventsSection(
                        deviceEvents = deviceEvents,
                        onScrolled = onDeviceEventsSectionScrolled,
                        onLongPressed = onDeviceEventLongPress
                    )
                }
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
        PullRefreshIndicator(
            refreshing = isLoading,
            state = refreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
    if (isDeleteEventAlertDialogOpened) {
        WarningBox(
            message = stringResource(R.string.user_device_event_delete_warning_message),
            onCancelClicked = onDeleteSelectedUserEventCancelled,
            onConfirmClicked = onDeleteSelectedUserEvent
        )
    }
    if (isDeleteUserDeviceAlertDialogOpened) {
        WarningBox(
            message = stringResource(R.string.user_device_delete_warning_message),
            onCancelClicked = onDeleteUserDeviceCancelled,
            onConfirmClicked = onDeleteUserDevice
        )
    }
    if (addNoteDialogOpened) {
        AddNoteDialog(
            title = noteTitle,
            content = noteContent,
            onTitleChange = onNoteTitleChanged,
            onContentChange = onNoteContentChanged,
            onAddClicked = onAddNote,
            onCancelClicked = onAddNoteCancelled
        )
    }
    if (addWateringDialogOpened) {
        AddWateringEventDialog(
            dateTime = LocalDateTime.now(),
            onDateTimeChange = {},
            onAddClicked = onAddWateringEvent,
            onCancelClicked = onAddWateringEventCancelled
        )
    }
    if (addPumpCleaningDialogOpened) {
        AddWateringEventDialog( // TODO: AddPumpCleaningEventDialog or generic
            dateTime = LocalDateTime.now(),
            onDateTimeChange = {},
            onAddClicked = onAddPumpCleaningEvent,
            onCancelClicked = onAddPumpCleaningEventCancelled
        )
    }
    error?.let {
        ErrorBoxCancelRetry(
            message = error,
            onCancel = onConfirmError,
            onRetry = onConfirmError
        )
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
                elevation = 4.dp,
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
                            Icon(Icons.AutoMirrored.Filled.List, null)
                        }
                        IconButton(
                            onClick = onChartSettingsClicked,
                            modifier = Modifier
                                .padding(top = 4.dp, end = 4.dp)
                                .then(Modifier.size(24.dp))
                        ) {
                            Icon(Icons.Filled.Settings, null)
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
                            options = LineChartDateType.entries
                                .map { it.mapToString(LocalContext.current) },
                            selectedIndex = selectedChartDateType.ordinal,
                            onOptionSelected = { onChartDateTypeSelected(LineChartDateType.entries[it]) }
                        )
                        CustomRadioGroup(
                            options = LineChartDataType.entries
                                .map { it.mapToString(LocalContext.current) },
                            selectedIndex = selectedChartDataType.ordinal,
                            onOptionSelected = { onChartDataTypeSelected(LineChartDataType.entries[it]) }
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
