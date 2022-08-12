package com.corrot.kwiatonomousapp.common.components

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

// TODO: Improve UI

@Composable
fun DeviceEventItem(deviceEvent: DeviceEvent) {
    Card(
        shape = RoundedCornerShape(8.dp),
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            when (deviceEvent) {
                is DeviceEvent.ConfigurationChange -> ConfigurationChangeEventItem(deviceEvent)
                is DeviceEvent.LowBattery -> LowBatteryEventItem(deviceEvent)
                is DeviceEvent.UserNote -> UserNoteEventItem(deviceEvent)
                is DeviceEvent.Watering -> WateringEventItem(deviceEvent)
            }
        }
    }
}

@Composable
fun LowBatteryEventItem(deviceEvent: DeviceEvent.LowBattery) {
    val extras = deviceEvent.extras as DeviceEventExtras.LowBattery

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        Image(
            painter = painterResource(id = R.drawable.low_battery),
            contentDescription = "",
            modifier = Modifier
                .size(48.dp)
                .padding(8.dp)
        )
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            DeviceIdAndDateTime(deviceId = deviceEvent.deviceId, timestamp = deviceEvent.timestamp)
            Divider(
                color = MaterialTheme.colors.primaryVariant, thickness = 1.dp,
                modifier = Modifier.padding(bottom = 8.dp, start = 8.dp, end = 8.dp)
            )
            Text(
                text = stringResource(R.string.battery_level_and_voltage_format)
                    .format(extras.batteryLevel, extras.batteryVoltage),
                style = MaterialTheme.typography.body1,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
private fun ConfigurationChangeEventItem(deviceEvent: DeviceEvent.ConfigurationChange) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        Image(
            painter = painterResource(id = R.drawable.settings),
            contentDescription = "",
            modifier = Modifier
                .size(48.dp)
                .padding(8.dp)
        )
        DeviceIdAndDateTime(deviceId = deviceEvent.deviceId, timestamp = deviceEvent.timestamp)
    }
}

@Composable
private fun UserNoteEventItem(deviceEvent: DeviceEvent.UserNote) {
    val extras = deviceEvent.extras as DeviceEventExtras.UserNote

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        Image(
            painter = painterResource(id = R.drawable.note),
            contentDescription = "",
            modifier = Modifier
                .size(48.dp)
                .padding(8.dp)
        )
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            DeviceIdAndDateTime(deviceId = deviceEvent.deviceId, timestamp = deviceEvent.timestamp)
            Divider(
                color = MaterialTheme.colors.primaryVariant, thickness = 1.dp,
                modifier = Modifier.padding(bottom = 8.dp, start = 8.dp, end = 8.dp)
            )
            Text(
                text = extras.title,
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Text(
                text = extras.content,
                style = MaterialTheme.typography.body2,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
private fun WateringEventItem(deviceEvent: DeviceEvent.Watering) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        Image(
            painter = painterResource(id = R.drawable.watering),
            contentDescription = "",
            modifier = Modifier
                .size(48.dp)
                .padding(8.dp)
        )
        DeviceIdAndDateTime(deviceId = deviceEvent.deviceId, timestamp = deviceEvent.timestamp)
    }
}

@Composable
private fun DeviceIdAndDateTime(deviceId: String, timestamp: LocalDateTime) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(text = deviceId, style = MaterialTheme.typography.body2)
        Text(text = timestamp.toFormattedString(), style = MaterialTheme.typography.body2)
    }
}

@Preview(
    "ConfigurationChangeEventItemPreviewLight",
    showSystemUi = false,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    heightDp = 450
)
@Composable
private fun ConfigurationChangeEventItemPreviewLight() {
    KwiatonomousAppTheme(darkTheme = false) {
        Surface {
            Column(Modifier.fillMaxSize().padding(8.dp)) {
                Divider(Modifier.height(8.dp))
                DeviceEventItem(DeviceEvent.Watering("deviceId", LocalDateTime.now()))
                Divider(Modifier.height(8.dp))
                DeviceEventItem(DeviceEvent.ConfigurationChange("deviceId", LocalDateTime.now()))
                Divider(Modifier.height(8.dp))
                DeviceEventItem(DeviceEvent.UserNote(
                    "userName",
                    "title",
                    "content",
                    "deviceId",
                    LocalDateTime.now())
                )
                Divider(Modifier.height(8.dp))
                DeviceEventItem(DeviceEvent.LowBattery(75, 3.75f, "deviceId", LocalDateTime.now()))
                Divider(Modifier.height(8.dp))
            }
        }
    }
}