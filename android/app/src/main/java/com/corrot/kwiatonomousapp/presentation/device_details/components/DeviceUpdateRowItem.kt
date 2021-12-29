package com.corrot.kwiatonomousapp.presentation.device_details.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.corrot.kwiatonomousapp.R
import com.corrot.kwiatonomousapp.common.components.TableCell
import com.corrot.kwiatonomousapp.common.toFormattedString
import com.corrot.kwiatonomousapp.domain.model.DeviceUpdate

@Composable
fun DeviceUpdateRowItem(
    deviceUpdate: DeviceUpdate,
    onRowClicked: (DeviceUpdate) -> Unit
) {
    Row(
        modifier = Modifier
            .height(IntrinsicSize.Min)
            .clickable { onRowClicked(deviceUpdate) }
    ) {
        TableCell(
            text = deviceUpdate.updateTime.toFormattedString(),
            weight = 0.3f
        )
        TableCell(
            text = stringResource(R.string.battery_format).format(
                deviceUpdate.batteryLevel,
                deviceUpdate.batteryVoltage
            ),
            weight = 0.3f
        )
        TableCell(
            text = stringResource(R.string.temperature_format).format(deviceUpdate.temperature),
            weight = 0.2f
        )
        TableCell(
            text = stringResource(R.string.humidity_format).format(deviceUpdate.humidity),
            weight = 0.2f
        )
    }
}
