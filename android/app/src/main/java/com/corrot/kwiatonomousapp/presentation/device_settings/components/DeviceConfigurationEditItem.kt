package com.corrot.kwiatonomousapp.presentation.device_settings.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.corrot.kwiatonomousapp.R
import com.corrot.kwiatonomousapp.common.components.DropdownableRow
import com.corrot.kwiatonomousapp.common.getAllUTCZones
import com.corrot.kwiatonomousapp.domain.model.DeviceConfiguration
import com.corrot.kwiatonomousapp.presentation.theme.KwiatonomousAppTheme
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter


@Composable
fun DeviceConfigurationEditItem(
    deviceConfiguration: DeviceConfiguration,
    nextWatering: LocalDateTime,
    onSleepTimeChanged: (Int) -> Unit,
    onTimeZoneChanged: (String) -> Unit,
    onWateringOnChanged: (Boolean) -> Unit,
    onWateringIntervalDaysChanged: (Int) -> Unit,
    onWateringAmountChanged: (Int) -> Unit,
    onWateringTimeChanged: (LocalTime) -> Unit,
    onWateringDateChanged: (Int, Int, Int) -> Unit,
) {
    var wateringTimePickerDialogOpened by remember { mutableStateOf(false) }
    var wateringDatePickerDialogOpened by remember { mutableStateOf(false) }

    if (wateringTimePickerDialogOpened) {
        TimePicker(
            title = stringResource(R.string.enter_watering_time),
            initialValue = deviceConfiguration.wateringTime,
            onDismiss = { wateringTimePickerDialogOpened = false },
            onConfirmClick = {
                onWateringTimeChanged(it)
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
        elevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            val onString = stringResource(R.string.on_abbr)
            val offString = stringResource(R.string.off_abbr)
            DropdownableRow(
                title = stringResource(R.string.sleep_time) + " (minutes)",
                currentValue = deviceConfiguration.sleepTimeMinutes,
                listOfValues = listOf(10, 30, 60),
                onValueChange = onSleepTimeChanged
            )
            DropdownableRow(
                title = stringResource(R.string.time_zone),
                currentValue = "UTC${deviceConfiguration.timeZoneOffset}",
                listOfValues = getAllUTCZones(),
                onValueChange = onTimeZoneChanged
            )
            DropdownableRow(
                title = stringResource(R.string.watering),
                currentValue = if (deviceConfiguration.wateringOn) onString else offString,
                listOfValues = listOf(onString, offString),
                onValueChange = { newWateringOn -> onWateringOnChanged(newWateringOn == onString) }
            )
            DropdownableRow(
                title = stringResource(R.string.watering_interval) + " (days)",
                currentValue = deviceConfiguration.wateringIntervalDays,
                listOfValues = listOf(1, 2, 3, 4, 5, 6, 7, 14, 28),
                onValueChange = onWateringIntervalDaysChanged
            )
            DropdownableRow(
                title = stringResource(R.string.watering_amount) + " (ml)",
                currentValue = deviceConfiguration.wateringAmount,
                listOfValues = listOf(50, 100, 150, 250),
                onValueChange = onWateringAmountChanged
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.watering_time) + ": ~${deviceConfiguration.wateringTime}",
                    style = MaterialTheme.typography.body1
                )
                OutlinedButton(
                    modifier = Modifier.width(150.dp),
                    onClick = {
                        wateringTimePickerDialogOpened = true
                    }
                ) {
                    Text(stringResource(R.string.change))
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
                    text = stringResource(R.string.next_watering_date) + ": \n${nextWatering.format(DateTimeFormatter.ISO_DATE)}",
                    style = MaterialTheme.typography.body1
                )
                OutlinedButton(
                    modifier = Modifier.width(150.dp),
                    onClick = {
                        wateringDatePickerDialogOpened = true
                    }
                ) {
                    Text(stringResource(R.string.change))
                }
            }
        }
    }
}

@Preview(
    "DeviceConfigurationEditPreviewLight",
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
                    .padding(12.dp)
            ) {
                item {
                    DeviceConfigurationEditItem(
                        deviceConfiguration = DeviceConfiguration(
                            deviceId = "test_id",
                            sleepTimeMinutes = 30,
                            timeZoneOffset = ZoneOffset.ofHours(1),
                            wateringOn = true,
                            wateringIntervalDays = 2,
                            wateringAmount = 200,
                            wateringTime = LocalTime.of(12, 30)
                        ),
                        nextWatering = LocalDateTime.now(),
                        onSleepTimeChanged = {},
                        onTimeZoneChanged = {},
                        onWateringAmountChanged = {},
                        onWateringIntervalDaysChanged = {},
                        onWateringOnChanged = {},
                        onWateringTimeChanged = {},
                        onWateringDateChanged = { _, _, _ -> }
                    )
                }
            }
        }
    }
}