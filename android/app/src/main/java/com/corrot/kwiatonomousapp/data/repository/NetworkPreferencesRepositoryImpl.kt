package com.corrot.kwiatonomousapp.data.repository

import android.util.Base64
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.corrot.kwiatonomousapp.common.Result
import com.corrot.kwiatonomousapp.data.local.datastore.NetworkPreferencesDataStoreKeys.HA1_KEY
import com.corrot.kwiatonomousapp.data.local.datastore.NetworkPreferencesDataStoreKeys.LAST_NONCE_KEY
import com.corrot.kwiatonomousapp.data.local.datastore.NetworkPreferencesDataStoreKeys.LOGIN_KEY
import com.corrot.kwiatonomousapp.domain.model.NetworkPreferences
import com.corrot.kwiatonomousapp.domain.repository.NetworkPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NetworkPreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : NetworkPreferencesRepository {

    override fun getNetworkPreferences(): Flow<Result<NetworkPreferences>> = dataStore.data
        .catch { error ->
            Result.Error(error)
        }
        .map { preferences ->
            Result.Success(
                NetworkPreferences(
                    login = preferences[LOGIN_KEY] ?: "",
                    ha1 = preferences[HA1_KEY] ?: "",
                    lastNonce = preferences[LAST_NONCE_KEY] ?: "",
                )
            )
        }

    override suspend fun updateNetworkPreferences(newNetworkPreferences: NetworkPreferences) {
        dataStore.edit { preferences ->
            preferences[LAST_NONCE_KEY] = newNetworkPreferences.lastNonce
        }
    }

    override suspend fun clearCredentials() {
        dataStore.edit { preferences ->
            preferences[LOGIN_KEY] = ""
            preferences[HA1_KEY] = ""
        }
    }

    override fun getLogin(): Flow<String> = dataStore.data
        .map { preferences ->
            preferences[LOGIN_KEY] ?: ""
        }

    override suspend fun updateLogin(login: String) {
        dataStore.edit { preferences ->
            preferences[LOGIN_KEY] = login
        }
    }

    override fun getHa1(): Flow<String> = dataStore.data
        .map { preferences ->
            preferences[HA1_KEY] ?: ""
        }

    override suspend fun updateHa1(ha1: String) {
        dataStore.edit { preferences ->
            preferences[HA1_KEY] = ha1
        }
    }

    override fun getLastNonce(): Flow<String> = dataStore.data
        .map { preferences ->
            preferences[LAST_NONCE_KEY] ?: ""
        }

    override suspend fun updateLastNonce(lastNonce: String) {
        dataStore.edit { preferences ->
            preferences[LAST_NONCE_KEY] = lastNonce
        }
    }
}