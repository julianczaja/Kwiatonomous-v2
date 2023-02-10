package com.corrot.kwiatonomousapp.presentation.app_settings

import android.annotation.SuppressLint
import android.content.res.Configuration.UI_MODE_NIGHT_NO
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.corrot.kwiatonomousapp.R
import com.corrot.kwiatonomousapp.common.components.*
import com.corrot.kwiatonomousapp.domain.NotificationsManager
import com.corrot.kwiatonomousapp.domain.model.AppTheme
import com.corrot.kwiatonomousapp.domain.model.ChartSettings
import com.corrot.kwiatonomousapp.domain.model.KwiatonomousAppState
import com.corrot.kwiatonomousapp.domain.model.NotificationsSettings
import com.corrot.kwiatonomousapp.presentation.device_settings.components.TimePicker
import com.corrot.kwiatonomousapp.presentation.theme.KwiatonomousAppTheme

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AppSettingsScreen(
    kwiatonomousAppState: KwiatonomousAppState,
    notificationsManager: NotificationsManager,
    viewModel: AppSettingsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val state = viewModel.state.value
    var clearDevicesCacheAlertDialogOpened by remember { mutableStateOf(false) }
    var timePickerDialogOpened by remember { mutableStateOf(false) }

    LaunchedEffect(true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                AppSettingsViewModel.Event.SHOW_DELETE_DONE_SNACKBAR -> {
                    kwiatonomousAppState.showSnackbar(context.getString(R.string.cache_cleared))
                }
            }
        }
    }
    DefaultScaffold(
        scaffoldState = kwiatonomousAppState.scaffoldState,
        topBar = {
            DefaultTopAppBar(
                title = stringResource(R.string.application_settings),
                onNavigateBackClicked = { kwiatonomousAppState.navController.popBackStack() }
            )
        }
    ) {
        AppSettingsScreenContent(
            appTheme = state.appTheme,
            chartSettings = state.chartSettings,
            notificationsSettings = state.notificationsSettings,
            onAppThemeSelected = viewModel::setAppTheme,
            onChartSettingsChanged = viewModel::setChartSettings,
            onNotificationsSettingsChanged = viewModel::setNotificationsSettings,
            onClearCacheButtonClicked = { clearDevicesCacheAlertDialogOpened = true },
            onTestLowBatteryNotificationClicked = {
                notificationsManager.sendBatteryNotification(
                    context = context,
                    deviceName = "testid",
                    notificationId = "testid".hashCode(),
                )
            },
            onTestPumpCleaningNotificationClicked = {
                notificationsManager.sendPumpCleaningReminderNotification(
                    context = context,
                    deviceId = "testid",
                    deviceName = "testid",
                    notificationId = "testid".hashCode(),
                    isTest = true
                )
            },
            onChangeNotificationsTimeClicked = { timePickerDialogOpened = true }
        )
        if (state.isLoading) {
            CircularProgressIndicator()
        }
        if (clearDevicesCacheAlertDialogOpened) {
            WarningBox(
                message = stringResource(R.string.remove_devices_cache_warning_message),
                onCancelClicked = {
                    clearDevicesCacheAlertDialogOpened = false
                },
                onConfirmClicked = {
                    clearDevicesCacheAlertDialogOpened = false
                    viewModel.clearDeviceUpdatesCache()
                }
            )
        }
        if (timePickerDialogOpened && state.notificationsSettings != null) {
            TimePicker(
                title = stringResource(R.string.enter_notifications_time),
                initialValue = state.notificationsSettings.notificationsTime,
                onDismiss = { timePickerDialogOpened = false },
                onConfirmClick = {
                    viewModel.setNotificationsTime(it)
                    timePickerDialogOpened = false
                }
            )
        }
        state.error?.let { error ->
            ErrorBoxCancelRetry(
                message = error,
                onCancel = { /* TODO */ },
                onRetry = { /* TODO */ }
            )
        }
    }
}

@Composable
private fun AppSettingsScreenContent(
    appTheme: AppTheme?,
    chartSettings: ChartSettings?,
    notificationsSettings: NotificationsSettings?,
    onAppThemeSelected: (AppTheme) -> Unit,
    onChartSettingsChanged: (ChartSettings) -> Unit,
    onNotificationsSettingsChanged: (NotificationsSettings) -> Unit,
    onClearCacheButtonClicked: () -> Unit,
    onTestLowBatteryNotificationClicked: () -> Unit,
    onTestPumpCleaningNotificationClicked: () -> Unit,
    onChangeNotificationsTimeClicked: () -> Unit,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }
        appTheme?.let {
            item {
                AppThemeSection(
                    currentAppTheme = appTheme,
                    onAppThemeSelected = onAppThemeSelected
                )
            }
        }
        chartSettings?.let {
            item {
                ChartSettingsSection(
                    currentChartSettings = chartSettings,
                    onChartSettingsChanged = onChartSettingsChanged
                )
            }
        }
        notificationsSettings?.let {
            item {
                NotificationsSettingsSection(
                    currentNotificationsSettings = notificationsSettings,
                    onNotificationsSettingsChanged = onNotificationsSettingsChanged,
                    onChangeNotificationsTimeClicked = onChangeNotificationsTimeClicked
                )
            }
        }
        item {
            Divider(
                color = MaterialTheme.colors.primaryVariant, thickness = 1.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        item {
            DeveloperSettingsSection(
                onClearCacheButtonClicked = onClearCacheButtonClicked,
                onTestLowBatteryNotificationClicked = onTestLowBatteryNotificationClicked,
                onTestPumpCleaningNotificationClicked = onTestPumpCleaningNotificationClicked
            )
        }
        item { Spacer(modifier = Modifier.height(8.dp)) }
    }
}

@Composable
private fun AppThemeSection(
    currentAppTheme: AppTheme,
    onAppThemeSelected: (AppTheme) -> Unit,
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Text(stringResource(R.string.application_theme), style = MaterialTheme.typography.h5)
            Divider(
                color = MaterialTheme.colors.primaryVariant, thickness = 1.dp,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.theme_sync_with_device))
                RadioButton(
                    selected = currentAppTheme == AppTheme.AUTO,
                    onClick = { onAppThemeSelected(AppTheme.AUTO) }
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.theme_light))
                RadioButton(
                    selected = currentAppTheme == AppTheme.LIGHT,
                    onClick = { onAppThemeSelected(AppTheme.LIGHT) }
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(stringResource(R.string.theme_dark))
                RadioButton(
                    selected = currentAppTheme == AppTheme.DARK,
                    onClick = { onAppThemeSelected(AppTheme.DARK) }
                )
            }
        }
    }
}

@Composable
private fun ChartSettingsSection(
    currentChartSettings: ChartSettings,
    onChartSettingsChanged: (ChartSettings) -> Unit,
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Text(stringResource(R.string.chart_settings), style = MaterialTheme.typography.h5)
            Divider(
                color = MaterialTheme.colors.primaryVariant, thickness = 1.dp,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            AppSettingsToggleItem(
                title = stringResource(R.string.render_drop_lines),
                isChecked = currentChartSettings.renderDropLines,
                onToggleClicked = {
                    onChartSettingsChanged(
                        currentChartSettings.copy(
                            renderDropLines = !currentChartSettings.renderDropLines
                        )
                    )
                }
            )
            AppSettingsToggleItem(
                title = stringResource(R.string.render_grid_lines),
                isChecked = currentChartSettings.renderGridLines,
                onToggleClicked = {
                    onChartSettingsChanged(
                        currentChartSettings.copy(
                            renderGridLines = !currentChartSettings.renderGridLines
                        )
                    )
                }
            )
        }
    }
}

@Composable
private fun NotificationsSettingsSection(
    currentNotificationsSettings: NotificationsSettings,
    onNotificationsSettingsChanged: (NotificationsSettings) -> Unit,
    onChangeNotificationsTimeClicked: () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Text(
                stringResource(R.string.notifications_settings),
                style = MaterialTheme.typography.h5
            )
            Divider(
                color = MaterialTheme.colors.primaryVariant, thickness = 1.dp,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            AppSettingsToggleItem(
                title = stringResource(R.string.notifications_on),
                isChecked = currentNotificationsSettings.notificationsOn,
                onToggleClicked = {
                    onNotificationsSettingsChanged(
                        currentNotificationsSettings.copy(
                            notificationsOn = !currentNotificationsSettings.notificationsOn
                        )
                    )
                }
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.notifications_time) + " (${currentNotificationsSettings.notificationsTime})")
                OutlinedButton(onClick = onChangeNotificationsTimeClicked) {
                    Text(text = stringResource(id = R.string.change))
                }
            }
        }
    }
}


@Composable
private fun DeveloperSettingsSection(
    onClearCacheButtonClicked: () -> Unit,
    onTestLowBatteryNotificationClicked: () -> Unit,
    onTestPumpCleaningNotificationClicked: () -> Unit,
) {
    ExpandableCardWithLabel(
        title = stringResource(R.string.developer_settings_label),
        initialExpandedState = false
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            OutlinedButton(onClick = onClearCacheButtonClicked) {
                Text(text = stringResource(R.string.developer_settings_clear_device_cache))
            }
            OutlinedButton(onClick = onTestLowBatteryNotificationClicked) {
                Text(text = stringResource(R.string.developer_settings_test_low_battery_notification))
            }
            OutlinedButton(onClick = onTestPumpCleaningNotificationClicked) {
                Text(text = stringResource(R.string.developer_settings_test_pump_cleaning_notification))
            }
        }
    }
}

@Preview(uiMode = UI_MODE_NIGHT_NO)
@Composable
private fun AppSettingsScreenContentLightPreview() {
    KwiatonomousAppTheme(darkTheme = false) {
        Surface {
            AppSettingsScreenContent(
                AppTheme.LIGHT,
                ChartSettings(),
                NotificationsSettings(),
                {}, {}, {}, {}, {}, {}, {}
            )
        }
    }
}
