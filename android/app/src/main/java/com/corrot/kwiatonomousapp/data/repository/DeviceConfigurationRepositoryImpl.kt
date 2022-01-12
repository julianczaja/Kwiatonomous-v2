package com.corrot.kwiatonomousapp.data.repository

import androidx.room.withTransaction
import com.corrot.kwiatonomousapp.data.local.database.KwiatonomousDatabase
import com.corrot.kwiatonomousapp.data.local.database.entity.DeviceConfigurationEntity
import com.corrot.kwiatonomousapp.data.local.database.entity.toDeviceConfiguration
import com.corrot.kwiatonomousapp.data.remote.api.KwiatonomousApi
import com.corrot.kwiatonomousapp.data.remote.dto.DeviceConfigurationDto
import com.corrot.kwiatonomousapp.domain.model.DeviceConfiguration
import com.corrot.kwiatonomousapp.domain.repository.DeviceConfigurationRepository
import kotlinx.coroutines.flow.Flow
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

    override fun getDeviceConfigurationFromDatabase(deviceId: String): Flow<DeviceConfiguration?> {
        return kwiatonomousDb.deviceConfigurationDao().getDeviceConfiguration(deviceId)
            .map { it?.toDeviceConfiguration() }
    }

    override suspend fun saveFetchedDeviceConfiguration(deviceConfiguration: DeviceConfigurationEntity) {
        kwiatonomousDb.withTransaction {
            kwiatonomousDb.deviceConfigurationDao().addDeviceConfiguration(deviceConfiguration)
        }
    }
}