package com.corrot.kwiatonomousapp.presentation.app_settings

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.corrot.kwiatonomousapp.R
import com.corrot.kwiatonomousapp.common.components.ErrorBoxCancelRetry
import com.corrot.kwiatonomousapp.presentation.theme.KwiatonomousAppTheme

@Composable
fun AppSettingsScreen(
    navController: NavController,
    viewModel: AppSettingsViewModel = hiltViewModel()
) {
    val state = viewModel.state.value

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            modifier = Modifier.height(45.dp),
            backgroundColor = MaterialTheme.colors.primary,
            title = { Text(text = stringResource(R.string.application_settings)) },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Filled.ArrowBack, "")
                }
            }
        )
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                state.appTheme?.let {
                    item {
                        AppThemeSection(
                            currentAppTheme = state.appTheme,
                            onAppThemeSelected = { viewModel.setAppTheme(it) }
                        )
                    }
                }
            }
            if (state.isLoading) {
                CircularProgressIndicator()
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

}

enum class AppTheme {
    AUTO,
    LIGHT,
    DARK
}


@Preview(uiMode = UI_MODE_NIGHT_NO, heightDp = 300)
@Composable
private fun AppThemeSectionLightPreview() {
    KwiatonomousAppTheme {
        Surface {
            AppThemeSection(
                currentAppTheme = AppTheme.AUTO,
                onAppThemeSelected = {}
            )
        }
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES, heightDp = 300)
@Composable
private fun AppThemeSectionDarkPreview() {
    KwiatonomousAppTheme {
        Surface {
            AppThemeSection(
                currentAppTheme = AppTheme.AUTO,
                onAppThemeSelected = {}
            )
        }
    }
}

@Composable
private fun AppThemeSection(
    currentAppTheme: AppTheme,
    onAppThemeSelected: (AppTheme) -> Unit
) {
    Card(
        elevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Text(stringResource(R.string.application_theme), style = MaterialTheme.typography.h2)
            Divider(
                color = MaterialTheme.colors.primaryVariant, thickness = 1.dp,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
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
                modifier = Modifier
                    .fillMaxWidth()
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
private fun AppSettingsToggleItem(
    title: String,
    isChecked: Boolean,
    onToggleClicked: (Boolean) -> Unit
) {
    Card(
        elevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                modifier = Modifier.padding(8.dp),
                text = title,
                style = MaterialTheme.typography.body1
            )

            Switch(
                colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colors.primary),
                checked = isChecked,
                onCheckedChange = onToggleClicked,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

