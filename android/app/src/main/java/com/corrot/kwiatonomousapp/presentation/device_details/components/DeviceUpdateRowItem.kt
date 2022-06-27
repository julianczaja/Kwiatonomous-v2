package com.corrot.kwiatonomousapp.presentation.device_details.components

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.corrot.kwiatonomousapp.R
import com.corrot.kwiatonomousapp.common.components.TableCell
import com.corrot.kwiatonomousapp.common.toFormattedString
import com.corrot.kwiatonomousapp.domain.model.DeviceUpdate
import com.corrot.kwiatonomousapp.presentation.theme.KwiatonomousAppTheme
import java.time.LocalDateTime

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
            text = stringResource(R.string.battery_level_and_voltage_format).format(
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

@Preview(
    name = "DeviceUpdateRowItemPreviewDark",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun DeviceUpdateRowItemPreviewDark() {
    KwiatonomousAppTheme(darkTheme = true) {
        Surface {
            DeviceUpdateRowItem(
                deviceUpdate = DeviceUpdate(
                    "testID",
                    LocalDateTime.now(),
                    77,
                    3.67f,
                    22.34f,
                    55.23f
                ),
                onRowClicked = {}
            )
        }
    }
}