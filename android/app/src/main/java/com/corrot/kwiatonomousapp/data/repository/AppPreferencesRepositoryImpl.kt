package com.corrot.kwiatonomousapp.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.corrot.kwiatonomousapp.common.Result
import com.corrot.kwiatonomousapp.data.local.datastore.AppPreferencesDataStoreKeys.APP_THEME_KEY
import com.corrot.kwiatonomousapp.data.local.datastore.AppPreferencesDataStoreKeys.CHART_SETTINGS_KEY
import com.corrot.kwiatonomousapp.data.local.datastore.AppPreferencesDataStoreKeys.FIRST_TIME_USER_KEY
import com.corrot.kwiatonomousapp.domain.model.AppPreferences
import com.corrot.kwiatonomousapp.domain.model.AppTheme
import com.corrot.kwiatonomousapp.domain.model.ChartSettings
import com.corrot.kwiatonomousapp.domain.repository.AppPreferencesRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AppPreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : AppPreferencesRepository {

    override fun getAppPreferences(): Flow<Result<AppPreferences>> = dataStore.data
        .catch { error ->
            Result.Error(error)
        }
        .map { preferences ->
            Result.Success(
                AppPreferences(
                    isFirstTimeUser = preferences[FIRST_TIME_USER_KEY] ?: false,
                    appTheme = AppTheme.values()[preferences[APP_THEME_KEY] ?: 0],
                    chartSettings = Gson().fromJson(
                        preferences[CHART_SETTINGS_KEY],
                        ChartSettings::class.java
                    ) ?: ChartSettings()
                )
            )
        }

    override suspend fun updateAppPreferences(newAppPreferences: AppPreferences) {
        dataStore.edit { preferences ->
            preferences[FIRST_TIME_USER_KEY] = newAppPreferences.isFirstTimeUser
            preferences[APP_THEME_KEY] = newAppPreferences.appTheme.ordinal
            preferences[CHART_SETTINGS_KEY] = Gson().toJson(newAppPreferences.chartSettings)
        }
    }

    override fun isFirstTimeUser(): Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[FIRST_TIME_USER_KEY] ?: false
        }

    override suspend fun updateFirstTimeUser(isFirstTimeUser: Boolean) {
        dataStore.edit { preferences ->
            preferences[FIRST_TIME_USER_KEY] = isFirstTimeUser
        }
    }

    override fun getAppTheme(): Flow<AppTheme> = dataStore.data
        .map { preferences ->
            AppTheme.values()[preferences[APP_THEME_KEY] ?: 0]
        }

    override suspend fun updateAppTheme(appTheme: AppTheme) {
        dataStore.edit { preferences ->
            preferences[APP_THEME_KEY] = appTheme.ordinal
        }
    }

    override fun getChartSettings(): Flow<ChartSettings> = dataStore.data
        .map { preferences ->
            Gson().fromJson(preferences[CHART_SETTINGS_KEY], ChartSettings::class.java)
                ?: ChartSettings()
        }

    override suspend fun updateChartSettings(chartSettings: ChartSettings) {
        dataStore.edit { preferences ->
            preferences[CHART_SETTINGS_KEY] = Gson().toJson(chartSettings)
        }
    }
}