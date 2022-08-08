package com.corrot.kwiatonomousapp.common.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.corrot.kwiatonomousapp.R
import com.corrot.kwiatonomousapp.common.toFormattedString
import com.corrot.kwiatonomousapp.domain.model.DeviceEvent
import com.corrot.kwiatonomousapp.domain.model.DeviceEventExtras

// TODO: Improve UI

@Composable
fun DeviceEventItem(deviceEvent: DeviceEvent) {
    Card {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
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
fun ConfigurationChangeEventItem(deviceEvent: DeviceEvent.ConfigurationChange) {
    Text(text = "Configuration change (${deviceEvent.timestamp.toFormattedString()})")
    Text(text = deviceEvent.deviceId)
}

@Composable
fun LowBatteryEventItem(deviceEvent: DeviceEvent.LowBattery) {
    val extras = deviceEvent.extras as DeviceEventExtras.LowBattery
    Text(text = "Low battery (${deviceEvent.timestamp.toFormattedString()})")
    Text(text = deviceEvent.deviceId)
    Text(text = stringResource(R.string.battery_level_and_voltage_format)
        .format(extras.batteryLevel, extras.batteryVoltage))
}

@Composable
fun UserNoteEventItem(deviceEvent: DeviceEvent.UserNote) {
    val extras = deviceEvent.extras as DeviceEventExtras.UserNote
    Text(text = "User note (${deviceEvent.timestamp.toFormattedString()})")
    Text(text = deviceEvent.deviceId)
    Text(text = extras.title)
    Text(text = extras.content)
}

@Composable
fun WateringEventItem(deviceEvent: DeviceEvent.Watering) {
    Text(text = "Watering (${deviceEvent.timestamp.toFormattedString()})")
    Text(text = deviceEvent.deviceId)
}