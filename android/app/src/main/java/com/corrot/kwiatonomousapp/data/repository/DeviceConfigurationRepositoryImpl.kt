package com.corrot.kwiatonomousapp.data.repository

import android.util.Log
import androidx.room.withTransaction
import com.corrot.kwiatonomousapp.data.local.database.KwiatonomousDatabase
import com.corrot.kwiatonomousapp.data.local.database.entity.DeviceConfigurationEntity
import com.corrot.kwiatonomousapp.data.local.database.entity.toDeviceConfiguration
import com.corrot.kwiatonomousapp.data.remote.api.KwiatonomousApi
import com.corrot.kwiatonomousapp.data.remote.dto.DeviceConfigurationDto
import com.corrot.kwiatonomousapp.domain.repository.DeviceConfigurationRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DeviceConfigurationRepositoryImpl @Inject constructor(
    private val kwiatonomousApi: KwiatonomousApi,
    private val kwiatonomousDb: KwiatonomousDatabase
) : DeviceConfigurationRepository {

    override suspend fun fetchDeviceConfigurationByDeviceId(deviceId: String): DeviceConfigurationDto {
        return kwiatonomousApi.getDeviceConfigurationByDeviceId(deviceId)
    }

    override suspend fun updateDeviceConfiguration(
        id: String,
        configuration: DeviceConfigurationDto
    ) {
        return kwiatonomousApi.updateDeviceConfiguration(id, configuration)
    }

    override fun getDeviceConfigurationFromDatabase(deviceId: String) =
        kwiatonomousDb.deviceConfigurationDao().getByDeviceId(deviceId)
            .map { it.toDeviceConfiguration() }
            // When database is empty null will be returned and `toDeviceConfiguration` will throw
            // exception. Let's catch it - this will also emit some kind of empty flow to notify
            // when we call `query().firstOrNull()` in networkBoundResource
            .catch { t ->
                Log.e("DeviceConfigurationRepositoryImpl", "getDeviceConfigurationFromDatabase: $t")
            }

    override suspend fun saveFetchedDeviceConfiguration(deviceConfiguration: DeviceConfigurationEntity) {
        kwiatonomousDb.withTransaction {
            kwiatonomousDb.deviceConfigurationDao().insertOrUpdate(deviceConfiguration)
        }
    }
}