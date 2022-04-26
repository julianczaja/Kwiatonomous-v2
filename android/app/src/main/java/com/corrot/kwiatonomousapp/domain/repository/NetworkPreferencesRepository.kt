package com.corrot.kwiatonomousapp.domain.repository

import com.corrot.kwiatonomousapp.common.Result
import com.corrot.kwiatonomousapp.domain.model.NetworkPreferences
import kotlinx.coroutines.flow.Flow

interface NetworkPreferencesRepository {

    fun getNetworkPreferences(): Flow<Result<NetworkPreferences>>

    suspend fun updateNetworkPreferences(newNetworkPreferences: NetworkPreferences)

    fun lastNonce(): Flow<String>

    suspend fun updateLastNonce(lastNonce: String)
}