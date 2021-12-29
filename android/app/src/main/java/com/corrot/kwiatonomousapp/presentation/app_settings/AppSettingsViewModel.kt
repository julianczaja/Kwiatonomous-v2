package com.corrot.kwiatonomousapp.presentation.app_settings

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corrot.kwiatonomousapp.domain.model.AppPreferences
import com.corrot.kwiatonomousapp.domain.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppSettingsViewModel @Inject constructor(
    private val appPreferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _state = mutableStateOf(AppSettingsState())
    val state: State<AppSettingsState> = _state

    init {
        getAppPreferences()
    }

    private fun getAppPreferences() {
        viewModelScope.launch {
            appPreferencesRepository.getAppPreferences().collect {
                _state.value = _state.value.copy(appPreferences = it)
            }
        }
    }

    fun updateAppPreferences(newAppPreferences: AppPreferences) {
        viewModelScope.launch {
            appPreferencesRepository.updateAppPreferences(newAppPreferences)
        }
    }
}
