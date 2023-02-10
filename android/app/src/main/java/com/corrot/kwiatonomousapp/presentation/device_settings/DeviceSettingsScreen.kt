package com.corrot.kwiatonomousapp.presentation.device_settings

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.corrot.kwiatonomousapp.R
import com.corrot.kwiatonomousapp.common.components.DefaultScaffold
import com.corrot.kwiatonomousapp.common.components.DefaultTopAppBar
import com.corrot.kwiatonomousapp.domain.model.DeviceConfiguration
import com.corrot.kwiatonomousapp.domain.model.KwiatonomousAppState
import com.corrot.kwiatonomousapp.presentation.device_settings.components.DeviceConfigurationEditItem
import com.corrot.kwiatonomousapp.presentation.theme.KwiatonomousAppTheme
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset


@Composable
fun DeviceSettingsScreen(
    kwiatonomousAppState: KwiatonomousAppState,
    viewModel: DeviceSettingsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    DefaultScaffold(
        scaffoldState = kwiatonomousAppState.scaffoldState,
        topBar = {
            DefaultTopAppBar(
                title = stringResource(R.string.device_settings),
                onNavigateBackClicked = kwiatonomousAppState.navController::popBackStack
            )
        }
    ) { padding ->
        DeviceSettingsScreenContent(
            padding = padding,
            error = state.error,
            deviceConfiguration = state.deviceConfiguration,
            nextWatering = state.nextWatering,
            isLoading = state.isLoading,
            settingsChanged = viewModel.settingsChanged,
            onDeviceConfigurationChanged = viewModel::onDeviceConfigurationChanged,
            onDeviceTimeZoneChanged = viewModel::onDeviceTimeZoneChanged,
            onDeviceWateringTimeChanged = viewModel::onDeviceWateringTimeChanged,
            onDeviceWateringDateChanged = viewModel::onDeviceWateringDateChanged,
            onResetChangedClicked = viewModel::resetChanges,
            onSaveClicked = viewModel::saveNewDeviceConfiguration
        )
    }
}

@Composable
fun DeviceSettingsScreenContent(
    padding: PaddingValues,
    error: String?,
    deviceConfiguration: DeviceConfiguration?,
    nextWatering: LocalDateTime?,
    isLoading: Boolean,
    settingsChanged: Boolean,
    onDeviceConfigurationChanged: (DeviceConfiguration) -> Unit,
    onDeviceTimeZoneChanged: (String) -> Unit,
    onDeviceWateringTimeChanged: (LocalTime) -> Unit,
    onDeviceWateringDateChanged: (Int, Int, Int) -> Unit,
    onResetChangedClicked: () -> Unit,
    onSaveClicked: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp, horizontal = 12.dp)
    ) {
        if (deviceConfiguration != null && nextWatering != null) {
            item {
                DeviceConfigurationEditItem(
                    deviceConfiguration = deviceConfiguration,
                    nextWatering = nextWatering,
                    onTimeZoneChanged = onDeviceTimeZoneChanged,
                    onWateringTimeChanged = onDeviceWateringTimeChanged,
                    onWateringDateChanged = onDeviceWateringDateChanged,
                    onSleepTimeChanged = { onDeviceConfigurationChanged(deviceConfiguration.copy(sleepTimeMinutes = it)) },
                    onWateringOnChanged = { onDeviceConfigurationChanged(deviceConfiguration.copy(wateringOn = it)) },
                    onWateringIntervalDaysChanged = { onDeviceConfigurationChanged(deviceConfiguration.copy(wateringIntervalDays = it)) },
                    onWateringAmountChanged = { onDeviceConfigurationChanged(deviceConfiguration.copy(wateringAmount = it)) }
                )
            }
            if (settingsChanged) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Button(
                            modifier = Modifier.width(150.dp),
                            onClick = onResetChangedClicked
                        ) {
                            Text(stringResource(R.string.reset))
                        }
                        Button(
                            modifier = Modifier.width(150.dp),
                            onClick = onSaveClicked
                        ) {
                            Text(stringResource(R.string.save))
                        }
                    }
                }
            }
        }
        if (!error.isNullOrBlank()) {
            item {
                Text(
                    text = error,
                    textAlign = TextAlign.Center,
                )
            }
        }
        if (isLoading) {
            item {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Preview(
    "DeviceSettingsScreenContentPreviewLight",
    heightDp = 600,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun DeviceSettingsScreenContentPreviewLight() {
    KwiatonomousAppTheme(darkTheme = false) {
        Surface {
            DeviceSettingsScreenContent(
                padding = PaddingValues(),
                error = null,
                deviceConfiguration = DeviceConfiguration(
                    deviceId = "deviceId",
                    sleepTimeMinutes = 30,
                    timeZoneOffset = ZoneOffset.UTC,
                    wateringOn = true,
                    wateringIntervalDays = 2,
                    wateringAmount = 150,
                    wateringTime = LocalTime.of(12, 0)
                ),
                nextWatering = LocalDateTime.of(2022, 11, 15, 18, 0),
                isLoading = false,
                settingsChanged = true,
                {}, {}, {}, { _, _, _ -> }, {}, {}
            )
        }
    }
}
