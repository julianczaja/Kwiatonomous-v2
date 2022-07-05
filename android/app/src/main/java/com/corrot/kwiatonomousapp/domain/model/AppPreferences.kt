package com.corrot.kwiatonomousapp.domain.model

data class AppPreferences(
    val isFirstTimeUser: Boolean = false,
    val appTheme: AppTheme = AppTheme.AUTO,
    val chartSettings: ChartSettings,
    val notificationsSettings: NotificationsSettings
)