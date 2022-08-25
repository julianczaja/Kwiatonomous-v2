package com.corrot.kwiatonomousapp.presentation.app_settings

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corrot.kwiatonomousapp.common.Result
import com.corrot.kwiatonomousapp.domain.model.AppTheme
import com.corrot.kwiatonomousapp.domain.model.ChartSettings
import com.corrot.kwiatonomousapp.domain.model.NotificationsSettings
import com.corrot.kwiatonomousapp.domain.repository.AppPreferencesRepository
import com.corrot.kwiatonomousapp.domain.usecase.ClearDevicesCacheUseCase
import com.corrot.kwiatonomousapp.KwiatonomousWorkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class AppSettingsViewModel @Inject constructor(
    private val kwiatonomousWorkManager: KwiatonomousWorkManager,
    private val appPreferencesRepository: AppPreferencesRepository,
    private val clearDevicesCacheUseCase: ClearDevicesCacheUseCase,
) : ViewModel() {

    enum class Event {
        SHOW_DELETE_DONE_SNACKBAR
    }

    val state = mutableStateOf(AppSettingsState())
    val eventFlow = MutableSharedFlow<Event>()

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
                            notificationsSettings = appPreferences.data.notificationsSettings,
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

    fun setNotificationsSettings(settings: NotificationsSettings) = viewModelScope.launch {
        appPreferencesRepository.updateNotificationsSettings(settings)
        kwiatonomousWorkManager.setupWorkManager(settings)
    }

    fun setNotificationsTime(newNotificationsTime: LocalTime) {
        state.value.notificationsSettings?.let {
            setNotificationsSettings(it.copy(notificationsTime = newNotificationsTime))
        }
    }

    fun clearDeviceUpdatesCache() = viewModelScope.launch {
        clearDevicesCacheUseCase.execute()
        eventFlow.emit(Event.SHOW_DELETE_DONE_SNACKBAR)
    }
}
