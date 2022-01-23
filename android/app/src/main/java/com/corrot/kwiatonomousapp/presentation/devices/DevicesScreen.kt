package com.corrot.kwiatonomousapp.presentation.devices

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.corrot.kwiatonomousapp.common.components.DeviceItem
import com.corrot.kwiatonomousapp.common.components.ErrorBoxCancelRetry
import com.corrot.kwiatonomousapp.presentation.Screen
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun DevicesScreen(
    navController: NavController,
    viewModel: DevicesViewModel = hiltViewModel()
) {
    val state = viewModel.state.value

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            modifier = Modifier.height(45.dp),
            backgroundColor = MaterialTheme.colors.primary,
            title = { Text(text = "All devices") },
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
                state = rememberSwipeRefreshState(state.isLoading),
                onRefresh = {
                    viewModel.refreshDevices()
                }
            ) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(vertical = 16.dp, horizontal = 12.dp)
                ) {
                    LazyColumn(Modifier.fillMaxSize()) {
                        state.devices?.let {
                            items(it) { device ->
                                Box(
                                    modifier = Modifier.padding(bottom = 16.dp)
                                ) {
                                    DeviceItem(
                                        device = device,
                                        onItemClick = {
                                            navController.navigate(
                                                Screen.DeviceDetails.withArgs(
                                                    device.deviceId
                                                )
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
            if (!state.error.isNullOrBlank()) {
                ErrorBoxCancelRetry(
                    message = state.error,
                    onCancel = { viewModel.confirmError() },
                    onRetry = { viewModel.refreshDevices() }
                )
            }
        }
    }
}