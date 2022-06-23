package com.corrot.kwiatonomousapp.presentation.app_settings

import com.corrot.kwiatonomousapp.domain.model.AppTheme
import com.corrot.kwiatonomousapp.domain.model.ChartSettings

data class AppSettingsState(
    val appTheme: AppTheme? = null,
    val chartSettings: ChartSettings? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)