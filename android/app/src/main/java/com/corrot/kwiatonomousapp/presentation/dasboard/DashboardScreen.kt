package com.corrot.kwiatonomousapp.presentation.dasboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.corrot.kwiatonomousapp.R
import com.corrot.kwiatonomousapp.common.components.DeviceItem
import com.corrot.kwiatonomousapp.presentation.dasboard.components.DeviceUpdateHeaderRowItem
import com.corrot.kwiatonomousapp.presentation.dasboard.components.DeviceUpdateRowItem
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun DashboardScreen(
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
                    state.device?.let {
                        item {
                            Text(
                                text = stringResource(R.string.device),
                                style = MaterialTheme.typography.h2,
                                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                            )
                            DeviceItem(
                                device = it,
                                onItemClick = {}
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


