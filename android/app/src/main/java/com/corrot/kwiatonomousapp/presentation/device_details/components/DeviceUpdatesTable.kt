package com.corrot.kwiatonomousapp.presentation.device_details.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.corrot.kwiatonomousapp.R
import com.corrot.kwiatonomousapp.domain.model.DeviceUpdate


@Composable
fun DeviceUpdatesTable(
    deviceUpdates: List<DeviceUpdate>,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Card(
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                LazyColumn(
                    modifier = Modifier
                        .height(
                            when (LocalConfiguration.current.orientation) {
                                Configuration.ORIENTATION_LANDSCAPE -> 250.dp
                                else -> 500.dp
                            }
                        )
                        .fillMaxHeight()
                        .padding(16.dp)
                ) {
                    items(deviceUpdates) {
                        DeviceUpdateRowItem(deviceUpdate = it, onRowClicked = {})
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.padding(end = 16.dp, bottom = 8.dp)
                    ) {
                        Text(text = stringResource(id = R.string.ok))
                    }
                }
            }
        }
    }
}