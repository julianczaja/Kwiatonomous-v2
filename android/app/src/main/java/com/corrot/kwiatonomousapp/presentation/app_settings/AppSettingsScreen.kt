package com.corrot.kwiatonomousapp.presentation.app_settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.corrot.kwiatonomousapp.common.Result

@Composable
fun AppSettingsScreen(
    viewModel: AppSettingsViewModel = hiltViewModel()
) {
    val state = viewModel.state.value

    Scaffold {
        Column(
            Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {

                when (state.appPreferences) {
                    is Result.Loading -> {
                        item {
                            CircularProgressIndicator()
                        }
                    }
                    is Result.Success -> {
                        val appPreferences = state.appPreferences.data
                        item {
                            AppSettingsToggleItem("Dark mode enabled", appPreferences.isDarkMode) {
                                viewModel.updateAppPreferences(appPreferences.copy(isDarkMode = it))
                            }
                        }
                        item {
                            AppSettingsToggleItem(
                                "First time user",
                                appPreferences.isFirstTimeUser
                            ) {
                                viewModel.updateAppPreferences(appPreferences.copy(isFirstTimeUser = it))
                            }
                        }
                    }
                    is Result.Error -> {
                        item {
                            DefaultErrorBox(state.appPreferences.throwable.message)
                        }
                    }
                }
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
            Switch(checked = isChecked, onCheckedChange = onToggleClicked, Modifier.padding(8.dp))
        }
    }
}

@Composable
private fun DefaultErrorBox(errorText: String?) {
    Text(
        text = errorText ?: "Unknown error",
        textAlign = TextAlign.Center,
    )
    Divider(
        color = MaterialTheme.colors.primaryVariant, thickness = 1.dp,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}
