package com.corrot.kwiatonomousapp.presentation.devices

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.corrot.kwiatonomousapp.R
import com.corrot.kwiatonomousapp.common.components.DefaultTopAppBar
import com.corrot.kwiatonomousapp.common.components.ErrorBoxCancel
import com.corrot.kwiatonomousapp.common.components.UserDeviceItem
import com.corrot.kwiatonomousapp.KwiatonomousAppState
import com.corrot.kwiatonomousapp.presentation.Screen

@ExperimentalFoundationApi
@Composable
fun DevicesScreen(
    kwiatonomousAppState: KwiatonomousAppState,
    viewModel: DevicesViewModel = hiltViewModel()
) {
    val state = viewModel.state.value

    Scaffold(
        topBar = {
            DefaultTopAppBar(
                title = stringResource(R.string.all_devices),
                onNavigateBackClicked = { kwiatonomousAppState.navController.popBackStack() }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { kwiatonomousAppState.navController.navigate(Screen.AddEditUserDevice.route) }
            ) {
                Icon(Icons.Filled.Add, "")
            }
        }
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyVerticalGrid(
                cells = GridCells.Adaptive(minSize = 150.dp),
                contentPadding = PaddingValues(vertical = 8.dp, horizontal = 6.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                state.userDevices?.let {
                    items(it) { userDevice ->
                        Box(
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp)
                        ) {
                            UserDeviceItem(
                                userDevice = userDevice,
                                onItemClick = {
                                    kwiatonomousAppState.navController.navigate(
                                        Screen.DeviceDetails.withArgs(
                                            userDevice.deviceId
                                        )
                                    )
                                }
                            )
                        }
                    }
                }
            }
            if (state.isLoading) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    CircularProgressIndicator()
                }
            }
            if (!state.error.isNullOrBlank()) {
                ErrorBoxCancel(
                    message = state.error,
                    onCancel = { viewModel.confirmError() }
                )
            }
        }
    }
}