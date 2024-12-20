package com.corrot.kwiatonomousapp.presentation.device_details.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.corrot.kwiatonomousapp.R
import com.corrot.kwiatonomousapp.common.toFormattedString
import com.corrot.kwiatonomousapp.domain.model.DeviceUpdate

@Composable
fun DeviceUpdateItem(
    deviceUpdate: DeviceUpdate
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = 4.dp,
        backgroundColor = Color.Gray,
        modifier = Modifier
            .padding(top = 8.dp, bottom = 8.dp)
            .fillMaxWidth()
    ) {
        Column {
            Row(
                modifier = Modifier.padding(start = 12.dp, top = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.update_time) + ": ",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.body1,
                )
                Text(
                    text = deviceUpdate.updateTime.toFormattedString(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.body2,
                )
            }
            Row(
                modifier = Modifier.padding(start = 12.dp, top = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.battery_level) + ": ",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.body1,
                )
                Text(
                    text = "${deviceUpdate.batteryLevel}%",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.body2,
                )
            }
            Row(
                modifier = Modifier.padding(start = 12.dp, top = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.battery_voltage) + ": ",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.body1,
                )
                Text(
                    text = stringResource(R.string.battery_voltage_format).format(deviceUpdate.batteryVoltage),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.body2,
                )
            }
            Row(
                modifier = Modifier.padding(start = 12.dp, top = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.temperature) + ": ",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.body1,
                )
                Text(
                    text = stringResource(R.string.temperature_format).format(deviceUpdate.temperature),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.body2,
                )
            }
            Row(
                modifier = Modifier.padding(start = 12.dp, top = 4.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.humidity) + ": ",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.body1,
                )
                Text(
                    text = stringResource(R.string.humidity_format).format(deviceUpdate.humidity),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.body2,
                )
            }
        }
    }
}