package com.corrot.kwiatonomousapp.common.components

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.corrot.kwiatonomousapp.R
import com.corrot.kwiatonomousapp.common.toFormattedString
import com.corrot.kwiatonomousapp.domain.model.DeviceEvent
import com.corrot.kwiatonomousapp.domain.model.DeviceEventExtras
import com.corrot.kwiatonomousapp.presentation.theme.KwiatonomousAppTheme
import java.time.LocalDateTime

@Composable
fun DeviceEventItem(
    deviceName: String?,
    deviceEvent: DeviceEvent,
    onLongPressed: () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 8.dp,
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(
                onLongPress = { onLongPressed() }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            when (deviceEvent) {
                is DeviceEvent.ConfigurationChange -> ConfigurationChangeEventItem(deviceEvent, deviceName)
                is DeviceEvent.LowBattery -> LowBatteryEventItem(deviceEvent, deviceName)
                is DeviceEvent.UserNote -> UserNoteEventItem(deviceEvent, deviceName)
                is DeviceEvent.Watering -> WateringEventItem(deviceEvent, deviceName)
                is DeviceEvent.PumpCleaning -> PumpCleaningEventItem(deviceEvent, deviceName)
            }
        }
    }
}

@Composable
fun PumpCleaningEventItem(deviceEvent: DeviceEvent.PumpCleaning, deviceName: String?) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        EventImage(R.drawable.pump_cleaning)
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.pump_cleaning),
                style = MaterialTheme.typography.caption
            )
            DeviceNameAndDateTime(
                deviceName = deviceName,
                timestamp = deviceEvent.timestamp
            )
        }
    }
}

@Composable
fun LowBatteryEventItem(deviceEvent: DeviceEvent.LowBattery, deviceName: String?) {
    val extras = deviceEvent.extras as DeviceEventExtras.LowBattery

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        EventImage(R.drawable.low_battery)
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.low_battery),
                    style = MaterialTheme.typography.caption
                )
                DeviceNameAndDateTime(
                    deviceName = deviceName,
                    timestamp = deviceEvent.timestamp
                )
            }
            Divider(
                color = MaterialTheme.colors.primaryVariant, thickness = 1.dp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = stringResource(R.string.battery_level_and_voltage_format).format(extras.batteryLevel, extras.batteryVoltage),
                style = MaterialTheme.typography.body1
            )
        }
    }
}

@Composable
private fun ConfigurationChangeEventItem(deviceEvent: DeviceEvent.ConfigurationChange, deviceName: String?) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        EventImage(R.drawable.settings)
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.configuration_change),
                style = MaterialTheme.typography.caption
            )
            DeviceNameAndDateTime(
                deviceName = deviceName,
                timestamp = deviceEvent.timestamp
            )
        }
    }
}

@Composable
private fun UserNoteEventItem(deviceEvent: DeviceEvent.UserNote, deviceName: String?) {
    val extras = deviceEvent.extras as DeviceEventExtras.UserNote

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        EventImage(R.drawable.note)
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.note),
                    style = MaterialTheme.typography.caption
                )
                DeviceNameAndDateTime(
                    deviceName = deviceName,
                    timestamp = deviceEvent.timestamp
                )
            }
            Divider(
                color = MaterialTheme.colors.primaryVariant, thickness = 1.dp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = extras.title,
                style = MaterialTheme.typography.caption,
            )
            if (extras.content.isNotEmpty()) {
                Text(
                    text = extras.content,
                    style = MaterialTheme.typography.body2,
                )
            }
        }
    }
}

@Composable
private fun WateringEventItem(deviceEvent: DeviceEvent.Watering, deviceName: String?) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        EventImage(R.drawable.watering)
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.watering),
                style = MaterialTheme.typography.caption
            )
            DeviceNameAndDateTime(
                deviceName = deviceName,
                timestamp = deviceEvent.timestamp
            )
        }
    }
}

@Composable
private fun EventImage(@DrawableRes resId: Int) {
    Image(
        painter = painterResource(id = resId),
        contentDescription = null,
        modifier = Modifier.size(32.dp)
    )
}

@Composable
private fun DeviceNameAndDateTime(deviceName: String?, timestamp: LocalDateTime) {
    Column(
        horizontalAlignment = Alignment.End
    ) {
        deviceName?.let { Text(text = it, style = MaterialTheme.typography.body2) }
        Text(text = timestamp.toFormattedString(), style = MaterialTheme.typography.body2)
    }
}

@Preview(
    "ConfigurationChangeEventItemPreviewLight",
    showSystemUi = false,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    heightDp = 600
)
@Composable
private fun ConfigurationChangeEventItemPreviewLight() {
    KwiatonomousAppTheme(darkTheme = false) {
        Surface {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
            ) {
                DeviceEventItem(
                    deviceName = "Device name",
                    deviceEvent = DeviceEvent.Watering(
                        deviceId = "deviceId",
                        timestamp = LocalDateTime.now()
                    ),
                    onLongPressed = {}
                )
                DeviceEventItem(
                    deviceName = "Device name",
                    deviceEvent = DeviceEvent.ConfigurationChange(
                        deviceId = "deviceId",
                        timestamp = LocalDateTime.now()
                    ),
                    onLongPressed = {}
                )
                DeviceEventItem(
                    deviceName = "Device name",
                    deviceEvent = DeviceEvent.UserNote(
                        userName = "userName",
                        title = "Title",
                        content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
                        deviceId = "deviceId",
                        timestamp = LocalDateTime.now()
                    ),
                    onLongPressed = {}
                )
                DeviceEventItem(
                    deviceName = "Device name",
                    deviceEvent = DeviceEvent.LowBattery(
                        batteryLevel = 75,
                        batteryVoltage = 3.75f,
                        deviceId = "deviceId",
                        timestamp = LocalDateTime.now()
                    ),
                    onLongPressed = {}
                )
                DeviceEventItem(
                    deviceName = "Device name",
                    deviceEvent = DeviceEvent.PumpCleaning(
                        deviceId = "deviceId",
                        timestamp = LocalDateTime.now()
                    ),
                    onLongPressed = {}
                )
            }
        }
    }
}