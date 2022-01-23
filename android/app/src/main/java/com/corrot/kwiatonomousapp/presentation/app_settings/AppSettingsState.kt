package com.corrot.kwiatonomousapp.presentation.app_settings

data class AppSettingsState(
    val appTheme: AppTheme? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)