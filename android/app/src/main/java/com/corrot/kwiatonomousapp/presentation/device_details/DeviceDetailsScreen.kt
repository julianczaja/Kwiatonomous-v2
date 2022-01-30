package com.corrot.kwiatonomousapp.presentation.device_details

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.corrot.kwiatonomousapp.R
import com.corrot.kwiatonomousapp.common.components.*
import com.corrot.kwiatonomousapp.common.toLong
import com.corrot.kwiatonomousapp.domain.model.Device
import com.corrot.kwiatonomousapp.domain.model.DeviceConfiguration
import com.corrot.kwiatonomousapp.domain.model.DeviceUpdate
import com.corrot.kwiatonomousapp.presentation.Screen
import com.corrot.kwiatonomousapp.presentation.app_settings.AppTheme
import com.corrot.kwiatonomousapp.presentation.device_details.components.DeviceConfigurationItem
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun DeviceDetailsScreen(
    navController: NavController,
    viewModel: DeviceDetailsViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val isLoading = viewModel.isLoading
    val currentAppTheme = viewModel.currentAppTheme.collectAsState(initial = AppTheme.AUTO)
    val isDarkMode = when (currentAppTheme.value) {
        AppTheme.AUTO -> isSystemInDarkTheme()
        AppTheme.LIGHT -> false
        AppTheme.DARK -> true
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            modifier = Modifier.height(45.dp),
            backgroundColor = MaterialTheme.colors.primary,
            title = { Text(text = stringResource(R.string.device_details)) },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Filled.ArrowBack, "")
                }
            }
        )
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            SwipeRefresh(
                state = rememberSwipeRefreshState(isLoading),
                onRefresh = { viewModel.refreshData() }
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp)
                ) {
                    item { Spacer(Modifier.height(16.dp)) }

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
                                    navController.navigate(
                                        Screen.DeviceSettings.withArgs(
                                            deviceConfiguration.deviceId
                                        )
                                    )
                                }
                            )
                        }
                    }


                    if (!state.deviceUpdates.isNullOrEmpty()) {
                        item {
                            DeviceUpdatesSection(
                                deviceUpdates = state.deviceUpdates,
                                selectedChartDateType = viewModel.state.value.selectedChartDateType,
                                selectedDateRange = viewModel.state.value.selectedDateRange,
                                selectedChartDataType = viewModel.state.value.selectedChartDataType,
                                isDarkMode = isDarkMode,
                                onChartDateTypeSelected = {
                                    viewModel.onChartDateTypeSelected(
                                        LineChartDateType.valueOf(it)
                                    )
                                },
                                onChartDataTypeSelected = {
                                    viewModel.onChartDataTypeSelected(
                                        LineChartDataType.valueOf(it)
                                    )
                                }
                            )
                        }
                    }

                    item { Spacer(Modifier.height(16.dp)) }
                }
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
}


@Composable
private fun DeviceSection(device: Device) {
    DeviceItem(device)
    Divider(
        color = MaterialTheme.colors.primaryVariant, thickness = 1.dp,
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
        Text(
            text = stringResource(R.string.device_configuration),
            style = MaterialTheme.typography.h2,
            modifier = Modifier.padding(bottom = 8.dp)
        )
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
    onChartDateTypeSelected: (String) -> Unit,
    onChartDataTypeSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.last_updates),
            style = MaterialTheme.typography.h2,
            modifier = Modifier.padding(bottom = 8.dp)
        )
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
                        options = LineChartDateType.values().map { it.name },
                        selectedOption = selectedChartDateType.name,
                        onOptionSelected = { onChartDateTypeSelected(it) }
                    )
                    CustomRadioGroup(
                        options = LineChartDataType.values().map { it.name },
                        selectedOption = selectedChartDataType.name,
                        onOptionSelected = { onChartDataTypeSelected(it) }
                    )
                }
            }
        }
    }
}
