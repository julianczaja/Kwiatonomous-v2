package com.corrot.kwiatonomousapp.domain.repository

import com.corrot.kwiatonomousapp.common.Result
import com.corrot.kwiatonomousapp.domain.model.NetworkPreferences
import kotlinx.coroutines.flow.Flow

interface NetworkPreferencesRepository {

    fun getNetworkPreferences(): Flow<Result<NetworkPreferences>>

    suspend fun updateNetworkPreferences(newNetworkPreferences: NetworkPreferences)

    suspend fun clearCredentials()

    fun getLogin(): Flow<String>

    suspend fun updateLogin(login: String)

    fun getHa1(): Flow<String>

    suspend fun updateHa1(ha1: String)

    fun getLastNonce(): Flow<String>

    suspend fun updateLastNonce(lastNonce: String)
}