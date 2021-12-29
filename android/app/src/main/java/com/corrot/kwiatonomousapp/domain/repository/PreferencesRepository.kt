package com.corrot.kwiatonomousapp.domain.repository

import com.corrot.kwiatonomousapp.common.Result
import com.corrot.kwiatonomousapp.domain.model.AppPreferences
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {

    fun getAppPreferences(): Flow<Result<AppPreferences>>

    suspend fun updateAppPreferences(newAppPreferences: AppPreferences)

    fun isFirstTimeUser(): Flow<Boolean>

    suspend fun updateFirstTimeUser(isFirstTimeUser: Boolean)

    fun isDarkMode(): Flow<Boolean>

    suspend fun updateDarkMode(isDarkMode: Boolean)
}