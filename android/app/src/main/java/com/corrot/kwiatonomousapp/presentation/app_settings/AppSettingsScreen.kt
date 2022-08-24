package com.corrot.kwiatonomousapp.presentation.app_settings

import android.annotation.SuppressLint
import android.content.res.Configuration.UI_MODE_NIGHT_NO
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.corrot.kwiatonomousapp.KwiatonomousAppState
import com.corrot.kwiatonomousapp.R
import com.corrot.kwiatonomousapp.common.components.AppSettingsToggleItem
import com.corrot.kwiatonomousapp.common.components.DefaultTopAppBar
import com.corrot.kwiatonomousapp.common.components.ErrorBoxCancelRetry
import com.corrot.kwiatonomousapp.common.components.WarningBox
import com.corrot.kwiatonomousapp.domain.model.AppTheme
import com.corrot.kwiatonomousapp.domain.model.ChartSettings
import com.corrot.kwiatonomousapp.domain.model.NotificationsSettings
import com.corrot.kwiatonomousapp.presentation.theme.KwiatonomousAppTheme

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AppSettingsScreen(
    kwiatonomousAppState: KwiatonomousAppState,
    viewModel: AppSettingsViewModel = hiltViewModel(),
) {
    val state = viewModel.state.value
    var clearDevicesCacheAlertDialogOpened by remember { mutableStateOf(false) }

    LaunchedEffect(true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                AppSettingsViewModel.Event.SHOW_DELETE_DONE_SNACKBAR -> {
                    kwiatonomousAppState.showSnackbar("Cache cleared")
                }
            }
        }
    }

    Scaffold(
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
            onAppThemeSelected = { viewModel.setAppTheme(it) },
            onChartSettingsChanged = { viewModel.setChartSettings(it) },
            onNotificationsSettingsChanged = { viewModel.setNotificationsSettings(it) },
            onClearCacheButtonClicked = { clearDevicesCacheAlertDialogOpened = true }
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
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp, horizontal = 12.dp)
    ) {
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
                    onNotificationsSettingsChanged = onNotificationsSettingsChanged
                )
            }
        }

        item {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.error),
                    onClick = onClearCacheButtonClicked
                ) {
                    Text(text = "Clear devices cache")
                }
            }
        }
    }
}

@Composable
private fun AppThemeSection(
    currentAppTheme: AppTheme,
    onAppThemeSelected: (AppTheme) -> Unit,
) {
    Card(
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
) {
    Card(
        elevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Text(stringResource(R.string.notifications_settings),
                style = MaterialTheme.typography.h5)
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
                {}, {}, {}, {}
            )
        }
    }
}
