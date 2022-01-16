package com.corrot.kwiatonomousapp.presentation.device_details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.corrot.kwiatonomousapp.R
import com.corrot.kwiatonomousapp.common.components.*
import com.corrot.kwiatonomousapp.domain.model.Device
import com.corrot.kwiatonomousapp.domain.model.DeviceConfiguration
import com.corrot.kwiatonomousapp.domain.model.DeviceUpdate
import com.corrot.kwiatonomousapp.presentation.Screen
import com.corrot.kwiatonomousapp.presentation.device_details.components.DeviceConfigurationItem
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import java.time.ZoneOffset

@Composable
fun DeviceDetailsScreen(
    navController: NavController,
    viewModel: DeviceDetailsViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val isLoading = viewModel.isLoading

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
                    .padding(12.dp)
            ) {
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

                state.deviceUpdates?.let { deviceUpdates ->
                    item {
                        DeviceUpdatesSection(
                            deviceUpdates = deviceUpdates,
                            selectedChartDateType = viewModel.state.value.selectedChartDateType,
                            selectedDateRange = viewModel.state.value.selectedDateRange,
                            selectedChartDataType = viewModel.state.value.selectedChartDataType,
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

@Composable
private fun DeviceSection(device: Device) {
    DeviceItem(device)
    Divider(
        color = MaterialTheme.colors.primaryVariant, thickness = 1.dp,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
private fun DeviceConfigurationSection(
    deviceConfiguration: DeviceConfiguration,
    onEditClicked: () -> Unit
) {
    Column(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth()) {
            Column {
                Text(
                    text = stringResource(R.string.device_configuration),
                    style = MaterialTheme.typography.h2,
                    modifier = Modifier.padding(
                        top = 8.dp,
                        bottom = 8.dp
                    )
                )
            }
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                IconButton(onClick = { onEditClicked() }) {
                    Icon(Icons.Filled.Edit, "")
                }
            }
        }

        DeviceConfigurationItem(
            deviceConfiguration = deviceConfiguration
        )
        Divider(
            color = MaterialTheme.colors.primaryVariant,
            thickness = 1.dp,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

@Composable
private fun DeviceUpdatesSection(
    deviceUpdates: List<DeviceUpdate>,
    selectedChartDateType: LineChartDateType,
    selectedChartDataType: LineChartDataType,
    selectedDateRange: Pair<Long, Long>,
    onChartDateTypeSelected: (String) -> Unit,
    onChartDataTypeSelected: (String) -> Unit
) {
    Column(Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.last_updates),
            style = MaterialTheme.typography.h2,
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(vertical = 8.dp)
        ) {
            DateLineChart(
                xData = deviceUpdates.map {
                    it.updateTime.toEpochSecond(
                        ZoneOffset.UTC
                    )
                },
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
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CustomRadioGroup(
                options = LineChartDateType.values()
                    .map { it.name },
                selectedOption = selectedChartDateType.name,
                onOptionSelected = { onChartDateTypeSelected(it) }
            )
            CustomRadioGroup(
                options = LineChartDataType.values()
                    .map { it.name },
                selectedOption = selectedChartDataType.name,
                onOptionSelected = { onChartDataTypeSelected(it) }
            )
        }
    }
}
