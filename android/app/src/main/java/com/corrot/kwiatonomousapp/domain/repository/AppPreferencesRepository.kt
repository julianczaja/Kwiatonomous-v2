package com.corrot.kwiatonomousapp.domain.repository

import com.corrot.kwiatonomousapp.common.Result
import com.corrot.kwiatonomousapp.domain.model.AppPreferences
import com.corrot.kwiatonomousapp.presentation.app_settings.AppTheme
import kotlinx.coroutines.flow.Flow

interface AppPreferencesRepository {

    fun getAppPreferences(): Flow<Result<AppPreferences>>

    suspend fun updateAppPreferences(newAppPreferences: AppPreferences)

    fun isFirstTimeUser(): Flow<Boolean>

    suspend fun updateFirstTimeUser(isFirstTimeUser: Boolean)

    fun getAppTheme(): Flow<AppTheme>

    suspend fun updateAppTheme(appTheme: AppTheme)
}