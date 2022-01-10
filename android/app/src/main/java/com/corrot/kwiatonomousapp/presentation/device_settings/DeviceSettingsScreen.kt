package com.corrot.kwiatonomousapp.presentation.device_settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.corrot.kwiatonomousapp.presentation.device_settings.components.DeviceConfigurationEditItem
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import java.time.LocalTime


@Composable
fun DeviceSettingsScreen(
    navController: NavController,
    viewModel: DeviceSettingsViewModel = hiltViewModel()
) {
    val state = viewModel.state.value

    Scaffold {
        SwipeRefresh(
            state = rememberSwipeRefreshState(state.isLoading),
            onRefresh = {
                viewModel.refreshData()
            }
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                item {
                    if (!state.error.isNullOrBlank()) {
                        Text(
                            text = state.error,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
                if (state.deviceConfiguration != null && state.nextWatering != null) {

                    item {
                        Text(
                            text = "Device settings",
                            style = MaterialTheme.typography.h2,
                        )
                        Divider(
                            color = MaterialTheme.colors.primaryVariant, thickness = 1.dp,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                        DeviceConfigurationEditItem(
                            deviceConfiguration = state.deviceConfiguration,
                            nextWatering = state.nextWatering,
                            onSleepTimeChanged = {
                                viewModel.onDeviceConfigurationChanged(
                                    state.deviceConfiguration.copy(
                                        sleepTimeMinutes = it
                                    )
                                )
                            },
                            onTimeZoneChanged = {
                                viewModel.onDeviceTimeZoneChanged(it)
                            },
                            onWateringOnChanged = {
                                viewModel.onDeviceConfigurationChanged(
                                    state.deviceConfiguration.copy(
                                        wateringOn = it
                                    )
                                )
                            },
                            onWateringIntervalDaysChanged = {
                                viewModel.onDeviceConfigurationChanged(
                                    state.deviceConfiguration.copy(
                                        wateringIntervalDays = it
                                    )
                                )
                            },
                            onWateringAmountChanged = {
                                viewModel.onDeviceConfigurationChanged(
                                    state.deviceConfiguration.copy(
                                        wateringAmount = it
                                    )
                                )
                            },
                            onWateringTimeChanged = { hour, minute ->
                                viewModel.onDeviceWateringTimeChanged(hour, minute)
                                viewModel.onDeviceConfigurationChanged(
                                    state.deviceConfiguration.copy(
                                        wateringTime = LocalTime.of(hour, minute)
                                    )
                                )
                            },
                            onWateringDateChanged = { year, month, dayOfMonth ->
                                viewModel.onDeviceWateringDateChanged(year, month, dayOfMonth)
                            }
                        )
                    }

                    // Show 'save' button only when changes were made
                    if (viewModel.settingsChanged) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Button(
                                    modifier = Modifier.width(150.dp),
                                    onClick = {
                                        viewModel.resetChanges()
                                    }
                                ) {
                                    Text("Reset")
                                }
                                Button(
                                    modifier = Modifier.width(150.dp),
                                    onClick = {
                                        viewModel.saveNewDeviceConfiguration()
                                    }
                                ) {
                                    Text("Save")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}