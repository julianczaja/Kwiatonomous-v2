package com.corrot.kwiatonomousapp.presentation.app_settings

import com.corrot.kwiatonomousapp.common.Result
import com.corrot.kwiatonomousapp.domain.model.AppPreferences

data class AppSettingsState(
    val appPreferences: Result<AppPreferences> = Result.Loading
)