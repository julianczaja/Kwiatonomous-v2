package com.corrot.kwiatonomousapp.presentation.app_settings

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corrot.kwiatonomousapp.common.Result
import com.corrot.kwiatonomousapp.domain.model.AppTheme
import com.corrot.kwiatonomousapp.domain.model.ChartSettings
import com.corrot.kwiatonomousapp.domain.repository.AppPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppSettingsViewModel @Inject constructor(
    private val appPreferencesRepository: AppPreferencesRepository
) : ViewModel() {

    val state = mutableStateOf(AppSettingsState())

    init {
        getAppPreferences()
    }

    private fun getAppPreferences() {
        viewModelScope.launch {
            appPreferencesRepository.getAppPreferences().collect { appPreferences ->
                when (appPreferences) {
                    is Result.Loading -> {
                        state.value = state.value.copy(isLoading = true)
                    }
                    is Result.Success -> {
                        state.value = state.value.copy(
                            appTheme = appPreferences.data.appTheme,
                            chartSettings = appPreferences.data.chartSettings,
                            isLoading = false,
                            error = null
                        )
                    }
                    is Result.Error -> {
                        state.value = state.value.copy(
                            isLoading = false,
                            error = appPreferences.throwable.message
                        )
                    }
                }
            }
        }
    }

    fun setAppTheme(appTheme: AppTheme) = viewModelScope.launch {
        appPreferencesRepository.updateAppTheme(appTheme)
    }


    fun setChartSettings(chartSettings: ChartSettings) = viewModelScope.launch {
        appPreferencesRepository.updateChartSettings(chartSettings)
    }
}
