package com.corrot.kwiatonomousapp.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.corrot.kwiatonomousapp.common.Result
import com.corrot.kwiatonomousapp.data.local.datastore.PreferencesDataStoreKeys.DARK_MODE_KEY
import com.corrot.kwiatonomousapp.data.local.datastore.PreferencesDataStoreKeys.FIRST_TIME_USER_KEY
import com.corrot.kwiatonomousapp.domain.model.AppPreferences
import com.corrot.kwiatonomousapp.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : PreferencesRepository {

    override fun getAppPreferences(): Flow<Result<AppPreferences>> = dataStore.data
        .catch { error ->
            Result.Error(error)
        }
        .map { preferences ->
            Result.Success(
                AppPreferences(
                    isFirstTimeUser = preferences[FIRST_TIME_USER_KEY] ?: false,
                    isDarkMode = preferences[DARK_MODE_KEY] ?: false
                )
            )
        }

    override suspend fun updateAppPreferences(newAppPreferences: AppPreferences) {
        dataStore.edit { preferences ->
            preferences[FIRST_TIME_USER_KEY] = newAppPreferences.isFirstTimeUser
            preferences[DARK_MODE_KEY] = newAppPreferences.isDarkMode
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

    override fun isDarkMode(): Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[DARK_MODE_KEY] ?: false
        }

    override suspend fun updateDarkMode(isDarkMode: Boolean) {
        dataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = isDarkMode
        }
    }
}