package com.corrot.kwiatonomousapp.presentation.device_details

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.corrot.kwiatonomousapp.KwiatonomousAppState
import com.corrot.kwiatonomousapp.R
import com.corrot.kwiatonomousapp.common.Constants
import com.corrot.kwiatonomousapp.common.components.*
import com.corrot.kwiatonomousapp.common.toLong
import com.corrot.kwiatonomousapp.domain.model.Device
import com.corrot.kwiatonomousapp.domain.model.DeviceConfiguration
import com.corrot.kwiatonomousapp.domain.model.DeviceUpdate
import com.corrot.kwiatonomousapp.domain.model.UserDevice
import com.corrot.kwiatonomousapp.presentation.Screen
import com.corrot.kwiatonomousapp.presentation.app_settings.AppTheme
import com.corrot.kwiatonomousapp.presentation.device_details.components.DeviceConfigurationItem
import com.corrot.kwiatonomousapp.presentation.device_details.components.DeviceItem
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun DeviceDetailsScreen(
    kwiatonomousAppState: KwiatonomousAppState,
    viewModel: DeviceDetailsViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val currentAppTheme = viewModel.currentAppTheme.collectAsState(initial = AppTheme.AUTO)
    val isDarkMode = when (currentAppTheme.value) {
        AppTheme.AUTO -> isSystemInDarkTheme()
        AppTheme.LIGHT -> false
        AppTheme.DARK -> true
    }
    var deleteAlertDialogOpened by remember { mutableStateOf(false) }

    LaunchedEffect(true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                DeviceDetailsViewModel.Event.NAVIGATE_UP -> {
                    kwiatonomousAppState.navController.navigateUp()
                }
                DeviceDetailsViewModel.Event.OPEN_EDIT_USER_DEVICE_SCREEN -> {
                    kwiatonomousAppState.navController.navigate(
                        Screen.AddEditUserDevice.withOptionalArg(
                            argName = Constants.NAV_ARG_DEVICE_ID,
                            argValue = viewModel.state.value.userDevice!!.deviceId // it can't be null there
                        )
                    )
                }
                DeviceDetailsViewModel.Event.SHOW_DELETE_ALERT_DIALOG -> {
                    deleteAlertDialogOpened = true
                }
            }
        }
    }

    Scaffold(
        topBar = {
            DefaultTopAppBar(
                title = stringResource(R.string.device_details),
                onNavigateBackClicked = { kwiatonomousAppState.navController.popBackStack() }
            )
        }
    ) {
        SwipeRefresh(
            state = rememberSwipeRefreshState(viewModel.isLoading),
            onRefresh = { viewModel.refreshData() }
        ) {
            LazyColumn(
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
                            isDarkMode = isDarkMode,
                            onChartDateTypeSelected = { viewModel.onChartDateTypeSelected(it) },
                            onChartDataTypeSelected = { viewModel.onChartDataTypeSelected(it) }
                        )
                    }
                }

                item { Spacer(Modifier.height(16.dp)) }
            }
        }
        if (deleteAlertDialogOpened) {
            WarningBox(
                message = stringResource(R.string.user_device_delete_warning_message),
                onCancelClicked = {
                    deleteAlertDialogOpened = false
                },
                onConfirmClicked = {
                    deleteAlertDialogOpened = false
                    viewModel.deleteUserDevice()
                }
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

enum class UserDeviceAction {
    EDIT, DELETE
}

@Composable
private fun UserDeviceSection(
    userDevice: UserDevice,
    lastUpdate: DeviceUpdate?,
    onActionClicked: (UserDeviceAction) -> Unit
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
    onEditClicked: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        ExpandableCardWithLabel(
            title = stringResource(R.string.device_configuration),
            initialExpandedState = false
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
    onChartDateTypeSelected: (LineChartDateType) -> Unit,
    onChartDataTypeSelected: (LineChartDataType) -> Unit
) {
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
                            isLoading = false, // TODO
                            fromDate = selectedDateRange.first,
                            toDate = selectedDateRange.second,
                            dateType = selectedChartDateType,
                            yAxisUnit = when (selectedChartDataType) {
                                LineChartDataType.TEMPERATURE -> "°C"
                                LineChartDataType.HUMIDITY -> "%"
                                LineChartDataType.BATTERY -> "V"
                            },
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
}
