package com.corrot.kwiatonomousapp.presentation.app_settings

import com.corrot.kwiatonomousapp.domain.model.AppTheme
import com.corrot.kwiatonomousapp.domain.model.ChartSettings
import com.corrot.kwiatonomousapp.domain.model.NotificationsSettings

data class AppSettingsState(
    val appTheme: AppTheme? = null,
    val chartSettings: ChartSettings? = null,
    val notificationsSettings: NotificationsSettings? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)