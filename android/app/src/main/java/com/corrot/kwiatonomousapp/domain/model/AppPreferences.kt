package com.corrot.kwiatonomousapp.domain.model

import com.corrot.kwiatonomousapp.presentation.app_settings.AppTheme

data class AppPreferences(
    val isFirstTimeUser: Boolean = false,
    val appTheme: AppTheme = AppTheme.AUTO
)