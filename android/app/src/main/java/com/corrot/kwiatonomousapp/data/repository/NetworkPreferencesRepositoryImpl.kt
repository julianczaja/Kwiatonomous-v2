package com.corrot.kwiatonomousapp.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.corrot.kwiatonomousapp.common.Result
import com.corrot.kwiatonomousapp.data.local.datastore.NetworkPreferencesDataStoreKeys.LAST_NONCE_KEY
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
                    lastNonce = preferences[LAST_NONCE_KEY] ?: "",
                )
            )
        }

    override suspend fun updateNetworkPreferences(newNetworkPreferences: NetworkPreferences) {
        dataStore.edit { preferences ->
            preferences[LAST_NONCE_KEY] = newNetworkPreferences.lastNonce
        }
    }

    override fun lastNonce(): Flow<String> = dataStore.data
        .map { preferences ->
            preferences[LAST_NONCE_KEY] ?: ""
        }

    override suspend fun updateLastNonce(lastNonce: String) {
        dataStore.edit { preferences ->
            preferences[LAST_NONCE_KEY] = lastNonce
        }
    }
}