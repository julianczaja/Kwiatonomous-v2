package com.corrot.kwiatonomousapp.presentation.dasboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.corrot.kwiatonomousapp.R
import com.corrot.kwiatonomousapp.presentation.dasboard.components.DeviceConfigurationItem
import com.corrot.kwiatonomousapp.common.components.DeviceItem
import com.corrot.kwiatonomousapp.common.components.lineChart
import com.corrot.kwiatonomousapp.presentation.Screen
import com.corrot.kwiatonomousapp.presentation.dasboard.components.DeviceUpdateHeaderRowItem
import com.corrot.kwiatonomousapp.presentation.dasboard.components.DeviceUpdateRowItem
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import java.time.ZoneOffset

@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state = viewModel.state.value

    Scaffold {
        Column(
            Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            if (!state.error.isNullOrBlank()) {
                Text(
                    text = state.error,
                    textAlign = TextAlign.Center,
                )
            }

            SwipeRefresh(
                state = rememberSwipeRefreshState(state.isLoading),
                onRefresh = {
                    viewModel.refreshDevice()
                }
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                ) {

                    // Device
                    if (state.device != null) {
                        item {
                            DeviceItem(
                                device = state.device,
                                onItemClick = {}
                            )
                            Divider(
                                color = MaterialTheme.colors.primaryVariant, thickness = 1.dp,
                                modifier = Modifier.padding(top = 16.dp)
                            )
                        }

                        // Device configuration
                        if (state.deviceConfiguration != null)
                            item {
                                Row(Modifier.fillMaxWidth()) {
                                    Column {
                                        Text(
                                            text = stringResource(R.string.device_configuration),
                                            style = MaterialTheme.typography.h2,
                                            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                                        )
                                    }
                                    Column(
                                        horizontalAlignment = Alignment.End,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    ) {
                                        IconButton(onClick = {
                                            navController.navigate(
                                                Screen.DeviceSettings.withArgs(
                                                    state.device.id
                                                )
                                            )
                                        }) {
                                            Icon(Icons.Filled.Edit, "")
                                        }
                                    }
                                }
                                DeviceConfigurationItem(
                                    deviceConfiguration = state.deviceConfiguration
                                )
                                Divider(
                                    color = MaterialTheme.colors.primaryVariant, thickness = 1.dp,
                                    modifier = Modifier.padding(top = 16.dp)
                                )
                            }
                    }


                    // Device updates
                    if (state.deviceUpdates.isNotEmpty()) {
                        item {
                            Text(
                                text = stringResource(R.string.last_updates),
                                style = MaterialTheme.typography.h2,
                                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                            )
                        }

                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .padding(vertical = 8.dp)
                            ) {
                                lineChart(
                                    xData = state.deviceUpdates.map {
                                        it.updateTime.toEpochSecond(
                                            ZoneOffset.UTC
                                        ).toFloat()
                                    },
                                    yData = state.deviceUpdates.map { it.temperature },
                                    title = "Temperature",
                                    yAxisUnit = "°C"
                                )
                            }
                        }

                        item {
                            Divider(color = MaterialTheme.colors.primaryVariant, thickness = 1.dp)
                        }
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .padding(vertical = 8.dp)
                            ) {
                                lineChart(
                                    xData = state.deviceUpdates.map {
                                        it.updateTime.toEpochSecond(
                                            ZoneOffset.UTC
                                        ).toFloat()
                                    },
                                    yData = state.deviceUpdates.map { it.humidity },
                                    title = "Humidity",
                                    yAxisUnit = "%"
                                )
                            }
                        }

                        item {
                            Divider(color = MaterialTheme.colors.primaryVariant, thickness = 1.dp)
                        }
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .padding(vertical = 8.dp)
                            ) {
                                lineChart(
                                    xData = state.deviceUpdates.map {
                                        it.updateTime.toEpochSecond(
                                            ZoneOffset.UTC
                                        ).toFloat()
                                    },
                                    yData = state.deviceUpdates.map { it.batteryVoltage },
                                    title = "Battery voltage",
                                    yAxisUnit = "V"
                                )
                            }
                        }

                        item {
                            Divider(
                                color = MaterialTheme.colors.primaryVariant,
                                thickness = 1.dp,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        item {
                            DeviceUpdateHeaderRowItem()
                        }

                        items(state.deviceUpdates) { deviceUpdate ->
                            DeviceUpdateRowItem(deviceUpdate, { /* TODO */ })
                        }
                    }
                }
            }
        }
    }
}


