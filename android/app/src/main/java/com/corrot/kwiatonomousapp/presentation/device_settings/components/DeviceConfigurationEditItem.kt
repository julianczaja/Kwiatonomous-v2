package com.corrot.kwiatonomousapp.presentation.device_settings.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.corrot.kwiatonomousapp.common.components.DropdownableRow
import com.corrot.kwiatonomousapp.domain.model.DeviceConfiguration
import com.corrot.kwiatonomousapp.presentation.theme.KwiatonomousAppTheme
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter


@Preview(
    "DeviceConfigurationEditPreviewLightA",
    heightDp = 500,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun DeviceConfigurationEditPreviewLight() {
    KwiatonomousAppTheme(darkTheme = false) {
        Surface {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                item {
                    DeviceConfigurationEditItem(
                        DeviceConfiguration(
                            30,
                            ZoneOffset.ofHours(1),
                            true,
                            2,
                            200,
                            LocalTime.of(12, 30)
                        ),
                        LocalDateTime.now(),
                        onSleepTimeChanged = {},
                        onTimeZoneChanged = {},
                        onWateringAmountChanged = {},
                        onWateringIntervalDaysChanged = {},
                        onWateringOnChanged = {},
                        onWateringTimeChanged = { hour, minute -> },
                        onWateringDateChanged = { year, month, day -> }
                    )
                }
            }
        }
    }
}

@Composable
fun DeviceConfigurationEditItem(
    deviceConfiguration: DeviceConfiguration,
    nextWatering: LocalDateTime,
    onSleepTimeChanged: (Int) -> Unit,
    onTimeZoneChanged: (String) -> Unit,
    onWateringOnChanged: (Boolean) -> Unit,
    onWateringIntervalDaysChanged: (Int) -> Unit,
    onWateringAmountChanged: (Int) -> Unit,
    onWateringTimeChanged: (Int, Int) -> Unit,
    onWateringDateChanged: (Int, Int, Int) -> Unit
) {
    var wateringTimePickerDialogOpened by remember { mutableStateOf(false) }
    var wateringDatePickerDialogOpened by remember { mutableStateOf(false) }

    if (wateringTimePickerDialogOpened) {
        WateringTimePicker(
            initialValue = deviceConfiguration.wateringTime,
            onDismiss = { wateringTimePickerDialogOpened = false },
            onConfirmClick = {
                onWateringTimeChanged(it.first, it.second)
                wateringTimePickerDialogOpened = false
            }
        )
    }

    if (wateringDatePickerDialogOpened) {
        WateringDatePicker(
            initialValue = nextWatering,
            onDismiss = { wateringDatePickerDialogOpened = false },
            onConfirmClick = {
                onWateringDateChanged(it.year, it.monthValue, it.dayOfMonth)
                wateringDatePickerDialogOpened = false
            }
        )
    }

    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column {
            DropdownableRow(
                title = "Sleep time (minutes)",
                currentValue = deviceConfiguration.sleepTimeMinutes,
                listOfValues = listOf(10, 30, 60),
                onValueChange = { newSleepTime -> onSleepTimeChanged(newSleepTime) }
            )
            DropdownableRow(
                title = "Time zone",
                currentValue = "UTC${deviceConfiguration.timeZoneOffset}",
                listOfValues = getAllUTCZones(),
                onValueChange = { newTimeZone -> onTimeZoneChanged(newTimeZone) }
            )
            DropdownableRow(
                title = "Watering",
                currentValue = if (deviceConfiguration.wateringOn) "On" else "Off",
                listOfValues = listOf("On", "Off"),
                onValueChange = { newWateringOn -> onWateringOnChanged(newWateringOn == "On") }
            )
            DropdownableRow(
                title = "Watering interval (days)",
                currentValue = deviceConfiguration.wateringIntervalDays,
                listOfValues = listOf(1, 2, 3, 4, 5, 6, 7, 14, 28),
                onValueChange = { newWateringInterval ->
                    onWateringIntervalDaysChanged(newWateringInterval)
                }
            )
            DropdownableRow(
                title = "Watering amount (ml)",
                currentValue = deviceConfiguration.wateringAmount,
                listOfValues = listOf(50, 100, 150, 250),
                onValueChange = { newWateringAmount -> onWateringAmountChanged(newWateringAmount) }
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Watering time: ~${deviceConfiguration.wateringTime}",
                    style = MaterialTheme.typography.body1
                )
                OutlinedButton(
                    modifier = Modifier.width(150.dp),
                    onClick = {
                        wateringTimePickerDialogOpened = true
                    }) {
                    Text("Change")
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Next watering date: \n${nextWatering.format(DateTimeFormatter.ISO_DATE)}",
                    style = MaterialTheme.typography.body1
                )
                OutlinedButton(
                    modifier = Modifier.width(150.dp),
                    onClick = {
                        wateringDatePickerDialogOpened = true
                    }) {
                    Text("Change")
                }
            }
        }
    }
}

private fun getAllUTCZones(): List<String> {
    val list = mutableListOf<String>()

    for (i in -12..14) {
        val format = when {
            i > 0 -> {
                "UTC%+03d:00"
            }
            i < 0 -> {
                "UTC%03d:00"
            }
            else -> {
                "UTC"
            }
        }
        list.add(String.format(format, i))
    }

    return list
}