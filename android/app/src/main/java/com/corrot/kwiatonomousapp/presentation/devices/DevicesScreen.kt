package com.corrot.kwiatonomousapp.presentation.devices

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.corrot.kwiatonomousapp.common.components.DeviceItem
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
        Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Text(
            text = "Devices",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h1,
        )
        SwipeRefresh(
            state = rememberSwipeRefreshState(state.isLoading),
            onRefresh = {
                viewModel.refreshDevices()
            }
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(top = 12.dp)
            ) {
                LazyColumn(Modifier.fillMaxSize()) {
                    items(state.devices) { device ->
                        DeviceItem(device = device, onItemClick = {
                            navController.navigate(Screen.DeviceDetails.withArgs(device.id))
                        })
                    }
                }
                if (!state.error.isNullOrBlank()) {
                    Text(
                        text = state.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}